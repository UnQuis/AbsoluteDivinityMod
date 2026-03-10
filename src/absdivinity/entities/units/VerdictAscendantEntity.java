package absdivinity.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import absdivinity.content.ADUnits;

/**
 * VerdictAscendant — T7 летающий босс.
 *
 * Механики:
 * 1. Три фазы боя (100% → 70% → 40% → смерть), каждая меняет поведение
 * 2. Телепортация (как EnergyUnit) — уходит от концентрированного огня
 * 3. Гравитационное притяжение (как EnergyUnitII.Clamper) — притягивает врагов
 * 4. Фазовый щит: при переходе фазы кратковременная неуязвимость (2 сек)
 * 5. Кап одиночного урона + DPS лимит
 * 6. Орбитальные дроны: призывает T5 летающих в конце каждой фазы
 * 7. Визуал: три орбитальных кольца, меняют цвет по фазе, гравитационные щупальца
 */
public class VerdictAscendantEntity extends UnitEntity {

    public static final float MAX_DPS    = 80000f;
    public static final float DPS_RESUME = MAX_DPS / 60f;
    public static final float MAX_ONCE   = 8000f;
    public float recentDamage = MAX_DPS;

    public int phase = 0;
    public float phaseShieldTimer = 0;
    public static final float PHASE_SHIELD_DURATION = 120f;

    public float teleportCooldown = 0;
    public static final float TELEPORT_RELOAD  = 300f;
    public static final float TELEPORT_RANGE   = 600f;
    public static final float TELEPORT_MIN     = 200f;
    protected Vec2 lastPos = new Vec2();

    public float gravPulse = 0;
    public static final float GRAV_FORCE = 80f;
    public static final float GRAV_RANGE = 350f;
    public static final float GRAV_RATE  = 45f;

    public int droneWave = 0;
    public static final float DRONE_SPAWN_INTERVAL = Time.toMinutes * 2.5f;
    public float droneReload = 0;

    public float ringRot1 = 0, ringRot2 = 0, ringRot3 = 0;
    public float pulseTimer = 0;
    protected Trail[] orbitalTrails = new Trail[3];

    protected Interval timer = new Interval(4);

    public static int typeId;

        @Override
        public int classId() {
            return typeId;
        }

    @Override
    public float mass() { return 12000000f; }

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        for (int i = 0; i < orbitalTrails.length; i++)
            orbitalTrails[i] = new Trail(type.trailLength + 12);
        lastPos.set(x, y);
    }

    @Override
    public void update() {
        super.update();

        pulseTimer += Time.delta;
        teleportCooldown -= Time.delta;
        gravPulse       -= Time.delta;
        droneReload     += Time.delta;
        phaseShieldTimer-= Time.delta;

        recentDamage = Math.min(recentDamage + DPS_RESUME * Time.delta, MAX_DPS);

        if (phase == 0 && healthf() < 0.7f) {
            enterPhase(1);
        } else if (phase == 1 && healthf() < 0.4f) {
            enterPhase(2);
        }

        if (!Vars.net.client() || isLocal()) {
            if (teleportCooldown <= 0 && hitTime > 0.5f) {
                performTeleport();
            }
        }

        if (phase >= 1 && gravPulse <= 0) {
            gravPulse = GRAV_RATE;
            Groups.unit.intersect(x - GRAV_RANGE, y - GRAV_RANGE,
                    GRAV_RANGE * 2, GRAV_RANGE * 2, u -> {
                if (u.team == team || u.dst(this) > GRAV_RANGE) return;
                Tmp.v1.set(x, y).sub(u.x, u.y).setLength(GRAV_FORCE * (phase + 1));
                u.impulseNet(Tmp.v1);
            });

            if (phase >= 2) {
                for (int i = 0; i < 3; i++)
                    Lightning.create(team, team.color, 200f, x, y, Mathf.random(360), 18);
            }
        }

        if (droneReload >= DRONE_SPAWN_INTERVAL) {
            droneReload = 0;
            droneWave++;
            int count = 2 + phase * 2;
            spawnDrones(count);
        }

        float speedMul = 1f + phase * 0.5f;
        ringRot1 += Time.delta * 1.2f * speedMul;
        ringRot2 -= Time.delta * 0.8f * speedMul;
        ringRot3 += Time.delta * 0.5f * speedMul;

        if (!Vars.headless) {
            for (int i = 0; i < orbitalTrails.length; i++) {
                float ang = ringRot1 + i * 120f;
                float rad = hitSize * (1.2f + i * 0.18f);
                Tmp.v1.trns(ang, rad).add(x, y);
                orbitalTrails[i].update(Tmp.v1.x, Tmp.v1.y, 1f);
            }
        }

        lastPos.set(x, y);
    }

    void enterPhase(int newPhase) {
        phase = newPhase;
        phaseShieldTimer = PHASE_SHIELD_DURATION;
        shieldAlpha = 1f;
        Fx.unitShieldBreak.at(x, y, 0f, team.color, this);
        for (int i = 0; i < 5; i++)
            Lightning.create(team, team.color, 250f, x, y, i * 72f, 20);
        spawnDrones(3 + newPhase * 2);
    }

    void performTeleport() {
        teleportCooldown = TELEPORT_RELOAD / (1f + phase * 0.4f);
        Unit closest = Units.closestEnemy(team, x, y, TELEPORT_RANGE * 2, u -> true);
        float angle = closest != null ? angleTo(closest) + 180f + Mathf.range(30f) : Mathf.random(360f);
        float dist  = Mathf.random(TELEPORT_MIN, TELEPORT_RANGE);
        Tmp.v2.trns(angle, dist).add(x, y);
        Tmp.v2.x = Mathf.clamp(Tmp.v2.x, 0, Vars.world.unitWidth());
        Tmp.v2.y = Mathf.clamp(Tmp.v2.y, 0, Vars.world.unitHeight());

        Fx.unitShieldBreak.at(x, y, hitSize * 0.5f, team.color, this);

        set(Tmp.v2.x, Tmp.v2.y);

        Fx.unitShieldBreak.at(x, y, hitSize * 0.5f, team.color, this);

        for (Trail t : orbitalTrails) t.clear();
    }

    void spawnDrones(int count) {
        UnitType droneType = phase >= 2 ? ADUnits.energyFalcon : ADUnits.energyHawk;
        for (int i = 0; i < count; i++) {
            float angle = (360f / count) * i;
            Tmp.v1.trns(angle, hitSize * 3f).add(x, y);
            droneType.spawn(team, Tmp.v1.x, Tmp.v1.y);
        }
    }

    @Override
    public void damage(float amount) {
        if (phaseShieldTimer > 0) return;
        rawDamageAscendant(amount);
    }

    public void rawDamageAscendant(float amount) {
        boolean hadShields = this.shield > 1e-4f;
        if (hadShields) this.shieldAlpha = 1f;

        amount = Math.min(amount, MAX_ONCE);

        float shieldDmg = Math.min(Math.max(shield, 0f), amount);
        shield -= shieldDmg;
        hitTime = 1f;
        amount -= shieldDmg;

        amount = Math.min(recentDamage / healthMultiplier, amount);
        recentDamage -= amount * 1.5f * healthMultiplier;

        if (amount > 0f && type.killable) {
            health -= amount;
            if (health <= 0f && !dead) kill();
            if (hadShields && shield <= 1e-4f)
                Fx.unitShieldBreak.at(x, y, 0f, team.color, this);
        }
    }

    @Override
    public void draw() {
        float z = Draw.z();
        Draw.z(Layer.bullet);

        Color phaseColor = phase == 0 ? team.color
                : phase == 1 ? Tmp.c1.set(team.color).lerp(Color.orange, 0.4f).cpy()
                : Tmp.c1.set(Color.red).lerp(team.color, 0.3f).cpy();

        if (phaseShieldTimer > 0) {
            float p = phaseShieldTimer / PHASE_SHIELD_DURATION;
            Draw.color(Color.white, phaseColor, 1f - p);
            Fill.circle(x, y, hitSize * (1.05f + p * 0.2f));
        }

        drawRing(x, y, hitSize * 1.25f, ringRot1, phaseColor, 2.5f);
        if (phase >= 1)
            drawRing(x, y, hitSize * 1.5f,  ringRot2, phaseColor, 2f);
        if (phase >= 2)
            drawRing(x, y, hitSize * 1.78f, ringRot3, phaseColor, 1.5f);

        for (int i = 0; i < (phase + 1) && i < orbitalTrails.length; i++) {
            float alpha = 0.6f + Mathf.absin(pulseTimer + i * 40f, 4f, 0.3f);
            Tmp.c1.set(phaseColor).a(alpha);
            orbitalTrails[i].draw(Tmp.c1, hitSize * 0.15f + i * 0.5f);
        }

        if (phase >= 1) {
            Draw.color(phaseColor, 0.5f + Mathf.absin(pulseTimer, 3f, 0.3f));
            Lines.stroke(2.5f);
            final int[] drawn = {0};
            Groups.unit.intersect(x - GRAV_RANGE * 0.6f, y - GRAV_RANGE * 0.6f,
                    GRAV_RANGE * 1.2f, GRAV_RANGE * 1.2f, u -> {
                if (u.team == team || drawn[0] >= 3 || u.dst(this) > GRAV_RANGE * 0.6f) return;
                drawn[0]++;
                drawGravLine(x, y, u.x, u.y, phaseColor);
            });
        }

        Draw.z(z);
        super.draw();
    }

    void drawRing(float cx, float cy, float radius, float rotation, Color color, float stroke) {
        float pulse = Mathf.absin(pulseTimer, 6f, 0.12f);
        Draw.color(color, 0.7f + pulse);
        Lines.stroke(stroke);
        int segs = 24;
        for (int i = 0; i < segs; i += 2) {
            float a1 = (i      / (float)segs) * Mathf.PI2 + rotation * Mathf.degreesToRadians;
            float a2 = ((i+1f) / (float)segs) * Mathf.PI2 + rotation * Mathf.degreesToRadians;
            Lines.line(
                cx + Mathf.cos(a1)*radius, cy + Mathf.sin(a1)*radius,
                cx + Mathf.cos(a2)*radius, cy + Mathf.sin(a2)*radius
            );
        }
    }

    void drawGravLine(float x1, float y1, float x2, float y2, Color color) {
        float dx = x2 - x1, dy = y2 - y1;
        float len = Mathf.len(dx, dy);
        float midX = (x1 + x2) / 2 + Mathf.sinDeg(pulseTimer * 2) * len * 0.15f;
        float midY = (y1 + y2) / 2 + Mathf.cosDeg(pulseTimer * 2) * len * 0.15f;
        Lines.curve(x1, y1, midX, midY, midX, midY, x2, y2, (int)(len / 12));
    }

    @Override
    public void read(Reads read) {
        phase = read.i();
        recentDamage = read.f();
        droneReload = read.f();
        droneWave = read.i();
        super.read(read);
    }

    @Override
    public void write(Writes write) {
        write.i(phase);
        write.f(recentDamage);
        write.f(droneReload);
        write.i(droneWave);
        super.write(write);
    }

    @Override
    public void readSync(Reads read) {
        super.readSync(read);
        if (!isLocal()) {
            phase = read.i();
            droneWave = read.i();
        } else {
            read.i(); read.i();
        }
    }

    @Override
    public void writeSync(Writes write) {
        super.writeSync(write);
        write.i(phase);
        write.i(droneWave);
    }
}