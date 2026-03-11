package absdivinity.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
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

public class VerdictAscendantEntity extends UnitEntity {

    public static final float MAX_DPS    = 80000f;
    public static final float DPS_RESUME = MAX_DPS / 60f;
    public static final float MAX_ONCE   = 10000f;
    public float recentDamage = MAX_DPS;

    public int phase = 0;
    public float immunityTimer  = 0f;
    public static final float IMMUNITY_DUR    = 120f;
    public static final float PHASE2_HP       = 0.70f;
    public static final float PHASE3_HP       = 0.40f;

    public float teleportReload = 0f;
    public static final float TELE_BASE_CD    = 280f;
    protected Vec2 lastPos = new Vec2();
    protected Trail[] teleTrails = new Trail[3];

    public float gravTimer = 0f;
    public static final float GRAV_INTERVAL  = 40f;
    public static final float GRAV_RANGE     = 400f;
    public static final float GRAV_FORCE     = 90f;

    public float dischargeTimer = 0f;
    public static final float DISCHARGE_CD   = 180f;
    public static final float DISCHARGE_DMG  = 280f;

    public float droneTimer = 0f;
    public static final float DRONE_CD       = Time.toMinutes * 2.5f;

    protected Trail[] rings = new Trail[5];
    public float[] ringRot  = new float[5];
    public float[] ringSpd  = { 1.3f, -0.9f, 0.55f, -1.6f, 0.7f };
    public float[] ringRad  = { 1.20f, 1.42f, 1.62f, 1.80f, 1.98f };

    public float pulse = 0f;
    protected static final Rand rand = new Rand();
    protected Interval timer = new Interval(4);

    public static int _classId = 0;
    @Override public int classId() { return _classId; }
    @Override public float mass()   { return 14_000_000f; }

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        for (int i = 0; i < rings.length; i++)
            rings[i] = new Trail(28 - i * 3);
        for (int i = 0; i < teleTrails.length; i++)
            teleTrails[i] = new Trail(type.trailLength + 8);
        lastPos.set(x, y);
    }

    @Override
    public void update() {
        super.update();

        pulse        += Time.delta;
        gravTimer    -= Time.delta;
        dischargeTimer -= Time.delta;
        droneTimer   -= Time.delta;
        teleportReload -= Time.delta;
        immunityTimer  -= Time.delta;

        recentDamage = Math.min(recentDamage + DPS_RESUME * Time.delta, MAX_DPS);

        if (phase == 0 && healthf() < PHASE2_HP) enterPhase(1);
        else if (phase == 1 && healthf() < PHASE3_HP) enterPhase(2);

        if (!Vars.net.client() || isLocal()) {
            boolean threatened = hitTime > 0.5f;
            if (!threatened && timer.get(0, 8f)) {
                int[] cnt = {0};
                Groups.bullet.intersect(x - 300, y - 300, 600, 600, b -> {
                    if (b.team != team) cnt[0]++;
                });
                threatened = cnt[0] > 5;
            }
            if (teleportReload <= 0 && threatened) {
                doTeleport();
            }
        }

        if (gravTimer <= 0) {
            gravTimer = GRAV_INTERVAL / (1f + phase * 0.4f);
            float force = GRAV_FORCE * (1f + phase);
            Groups.unit.intersect(x - GRAV_RANGE, y - GRAV_RANGE,
                    GRAV_RANGE * 2, GRAV_RANGE * 2, u -> {
                if (u.team == team || u.dst(this) > GRAV_RANGE) return;
                Tmp.v1.set(x, y).sub(u).setLength(force * (1f - u.dst(this) / GRAV_RANGE));
                u.impulseNet(Tmp.v1);
            });
        }

        if (phase >= 1 && dischargeTimer <= 0) {
            dischargeTimer = DISCHARGE_CD / (1f + phase * 0.5f);
            int count = 8 + phase * 6;
            for (int i = 0; i < count; i++) {
                Lightning.create(team, team.color, DISCHARGE_DMG,
                        x, y, i * (360f / count), Mathf.random(14, 28));
            }
        }

        if (droneTimer <= 0) {
            droneTimer = DRONE_CD;
            spawnDrones(2 + phase * 2);
        }

        int activeRings = 1 + phase * 2;
        for (int i = 0; i < activeRings && i < rings.length; i++) {
            ringRot[i] += Time.delta * ringSpd[i] * (1f + phase * 0.3f);
            float rad = hitSize * ringRad[i];
            Tmp.v1.trns(ringRot[i], rad).add(x, y);
            rings[i].update(Tmp.v1.x, Tmp.v1.y, 1f + Mathf.absin(pulse + i * 20f, 6f, 0.2f));
        }

        if (!Vars.headless) {
            for (int i = 0; i < teleTrails.length; i++) {
                float ang = pulse * 1.2f + i * 120f;
                Tmp.v1.trns(ang, hitSize * 0.65f).add(x, y);
                teleTrails[i].update(Tmp.v1.x, Tmp.v1.y, 1f);
            }
        }

        lastPos.set(x, y);
    }

    void doTeleport() {
        teleportReload = TELE_BASE_CD / (1f + phase * 0.35f);

        Unit closest = Units.closestEnemy(team, x, y, GRAV_RANGE * 2, u -> true);
        float angle = closest != null ? angleTo(closest) + 180f + Mathf.range(40f) : Mathf.random(360f);
        float dist  = Mathf.random(250f, 550f);
        Tmp.v2.trns(angle, dist).add(x, y);
        Tmp.v2.x = Mathf.clamp(Tmp.v2.x, 50, Vars.world.unitWidth()  - 50);
        Tmp.v2.y = Mathf.clamp(Tmp.v2.y, 50, Vars.world.unitHeight() - 50);

        Fx.unitShieldBreak.at(x, y, hitSize * 0.6f, team.color, this);
        Lightning.create(team, team.color, 150f, x, y, Mathf.random(360f), 15);

        set(Tmp.v2.x, Tmp.v2.y);

        Fx.unitShieldBreak.at(x, y, hitSize * 0.6f, team.color, this);
        for (Trail t : rings) t.clear();
        for (Trail t : teleTrails) t.clear();
    }

    void enterPhase(int newPhase) {
        phase = newPhase;
        immunityTimer = IMMUNITY_DUR;

        for (int i = 0; i < 12; i++)
            Lightning.create(team, team.color, DISCHARGE_DMG * 1.5f,
                    x, y, i * 30f, Mathf.random(20, 35));
        Fx.unitShieldBreak.at(x, y, 0f, team.color, this);

        teleportReload = 0;
        doTeleport();
        spawnDrones(4 + newPhase * 3);
        gravTimer = 0;
    }

    void spawnDrones(int count) {
        UnitType type = phase >= 2 ? ADUnits.energyFalcon : ADUnits.energyHawk;
        for (int i = 0; i < count; i++) {
            Tmp.v1.trns(i * (360f / count), hitSize * 2.8f).add(x, y);
            type.spawn(team, Tmp.v1.x, Tmp.v1.y);
        }
    }

    @Override
    public void damage(float amount) {
        if (immunityTimer > 0) return;
        ascendRawDamage(amount);
    }

    void ascendRawDamage(float amount) {
        boolean hadShield = shield > 1e-4f;
        if (hadShield) shieldAlpha = 1f;

        amount = Math.min(amount, MAX_ONCE);
        float sd = Math.min(Math.max(shield, 0f), amount);
        shield -= sd;
        hitTime = 1f;
        amount -= sd;

        amount = Math.min(recentDamage / healthMultiplier, amount);
        recentDamage -= amount * 1.5f * healthMultiplier;

        if (amount > 0f && type.killable) {
            health -= amount;
            if (health <= 0f && !dead) kill();
            if (hadShield && shield <= 1e-4f)
                Fx.unitShieldBreak.at(x, y, 0f, team.color, this);
        }
    }

    @Override
    public void draw() {
        float z = Draw.z();

        Color col = phaseColor();
        int activeRings = 1 + phase * 2;

        Draw.z(Layer.flyingUnit - 0.01f);
        for (int i = 0; i < activeRings && i < rings.length; i++) {
            float alpha = 0.5f + Mathf.absin(pulse + i * 25f, 6f, 0.3f);
            float width = (hitSize * 0.13f) * (1f - i * 0.12f);
            Tmp.c1.set(col).lerp(Color.white, i * 0.08f).a(alpha);
            rings[i].draw(Tmp.c1, width);
            rings[i].drawCap(Tmp.c1, width);
        }

        Draw.z(Layer.flyingUnit - 0.005f);
        for (int i = 0; i < teleTrails.length; i++) {
            float alpha = 0.35f + Mathf.absin(pulse + i * 50f, 4f, 0.2f);
            Tmp.c1.set(col).lerp(Color.black, 0.3f).a(alpha);
            teleTrails[i].draw(Tmp.c1, hitSize * 0.08f);
        }

        if (immunityTimer > 0) {
            float p = Interp.pow3Out.apply(immunityTimer / IMMUNITY_DUR);
            Draw.z(Layer.effect);
            Draw.color(Color.white, col, 1f - p * 0.55f);
            Draw.alpha(p * 0.8f);
            Fill.circle(x, y, hitSize * (1f + p * 0.4f));
        }

        if (phase >= 1) {
            Draw.z(Layer.flyingUnit - 0.02f);
            Lines.stroke(2.2f);
            float lineAlpha = 0.5f + Mathf.absin(pulse, 3f, 0.25f);
            Draw.color(col, lineAlpha);
            final int[] drawn = {0};
            Groups.unit.intersect(x - GRAV_RANGE * 0.7f, y - GRAV_RANGE * 0.7f,
                    GRAV_RANGE * 1.4f, GRAV_RANGE * 1.4f, u -> {
                if (u.team == team || drawn[0] >= 5 || u.dst(this) > GRAV_RANGE * 0.7f) return;
                drawn[0]++;
                float dx = u.x - x, dy = u.y - y;
                float len = Mathf.len(dx, dy);
                float midX = (x + u.x) / 2 + Mathf.sinDeg(pulse * 1.8f + drawn[0] * 60f) * len * 0.18f;
                float midY = (y + u.y) / 2 + Mathf.cosDeg(pulse * 1.8f + drawn[0] * 60f) * len * 0.18f;
                Lines.curve(x, y, midX, midY, midX, midY, u.x, u.y, Math.max(4, (int)(len / 10)));
            });
        }

        if (phase >= 2) {
            Draw.z(Layer.flyingUnit - 0.03f);
            float flicker = Mathf.absin(pulse * 2.5f, 2f, 1f);
            Draw.color(col, flicker * 0.35f);
            Fill.circle(x, y, hitSize * (0.9f + flicker * 0.15f));
        }

        Draw.z(z);
        super.draw();
    }

    Color phaseColor() {
        if (phase == 0) return team.color;
        if (phase == 1) return Tmp.c1.set(team.color).lerp(Color.valueOf("cc44ff"), 0.5f).cpy();
        return             Tmp.c1.set(Color.valueOf("ff1111")).lerp(Color.valueOf("bf92f9"), 0.4f).cpy();
    }

    @Override public void read(Reads r) { phase = r.i(); recentDamage = r.f(); teleportReload = r.f(); super.read(r); }
    @Override public void write(Writes w) { w.i(phase); w.f(recentDamage); w.f(teleportReload); super.write(w); }

    @Override
    public void readSync(Reads r) {
        super.readSync(r);
        if (!isLocal()) { phase = r.i(); }
        else r.i();
    }
    @Override
    public void writeSync(Writes w) { super.writeSync(w); w.i(phase); }
}