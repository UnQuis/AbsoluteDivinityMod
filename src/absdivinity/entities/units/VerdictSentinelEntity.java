package absdivinity.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.gen.Groups;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import absdivinity.content.ADUnits;

/**
 * VerdictSentinel — T7 наземный босс.
 *
 * Механики:
 * 1. Кап одиночного урона (как Nucleoid.rawDamage) — не берёт больше maxOnceDamage за удар
 * 2. Лимит урона в секунду (recentDamage) — становится неуязвимым при спаме урона
 * 3. Призыв T4 миньонов при 50% HP (один раз) и 20% HP (один раз)
 * 4. Молниевое поле: при получении урона бьёт молниями по ближайшим врагам
 * 5. Регенерация щита когда не под огнём (recentDamage высокий)
 * 6. Визуал: кольцо заряда миньонов + молнии вокруг корпуса
 */
public class VerdictSentinelEntity extends UnitEntity {

    public static final float MAX_DPS     = 50000f;
    public static final float DPS_RESUME  = MAX_DPS / 60f;
    public static final float MAX_ONCE    = 5000f;

    public float recentDamage = MAX_DPS;

    public int reinforcePhase = 0;
    public float reinforceReload = 0;
    public static final float REINFORCE_SPACING = Time.toMinutes * 3f;

    public float lightningCooldown = 0;
    public static final float LIGHTNING_RATE = 20f;
    public static final float LIGHTNING_RANGE = 220f;
    public static final float LIGHTNING_DAMAGE = 180f;

    public static final float SHIELD_REGEN_RATE = 15f;
    public static final float MAX_SHIELD = 12000f;

    public float pulseTimer = 0;

    public static int typeId;

        @Override
        public int classId() {
            return typeId;
        }

    @Override
    public float mass() { return 9000000f; }

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        shield = MAX_SHIELD;
    }

    @Override
    public void update() {
        super.update();

        pulseTimer += Time.delta;
        lightningCooldown -= Time.delta;

        recentDamage = Math.min(recentDamage + DPS_RESUME * Time.delta, MAX_DPS);

        if (recentDamage > MAX_DPS * 0.6f && shield < MAX_SHIELD) {
            shield = Math.min(shield + SHIELD_REGEN_RATE * Time.delta, MAX_SHIELD);
            shieldAlpha = Math.min(shieldAlpha + 0.02f, 1f);
        }

        if (reinforcePhase == 0 && healthf() < 0.5f) {
            reinforcePhase = 1;
            reinforceReload = 0;
            spawnReinforcements(4);
        }

        if (reinforcePhase == 1 && healthf() < 0.2f) {
            reinforcePhase = 2;
            spawnReinforcements(8);
            Fx.unitShieldBreak.at(x, y, 0f, team.color, this);
            Lightning.create(team, team.color, LIGHTNING_DAMAGE * 2, x, y, Mathf.random(360), 30);
        }

        reinforceReload += Time.delta;
        if (reinforcePhase >= 1 && healthf() < 0.35f && reinforceReload >= REINFORCE_SPACING) {
            reinforceReload = 0;
            spawnReinforcements(2);
        }

        if (hitTime > 0.5f && lightningCooldown <= 0) {
            lightningCooldown = LIGHTNING_RATE;
            int bolts = reinforcePhase >= 2 ? 6 : reinforcePhase == 1 ? 4 : 2;
            for (int i = 0; i < bolts; i++) {
                Lightning.create(team, team.color, LIGHTNING_DAMAGE,
                        x, y, Mathf.random(360), Mathf.random(8, 20));
            }
        }
    }

    void spawnReinforcements(int count) {
        for (int i = 0; i < count; i++) {
            float angle = (360f / count) * i + rotation;
            Tmp.v1.trns(angle, hitSize * 2.5f).add(x, y);
            UnitType minion = (i % 2 == 0) ? ADUnits.corruptedTitan : ADUnits.diviniteShield;
            minion.spawn(team, Tmp.v1.x, Tmp.v1.y);
        }
    }

    @Override
    public void damage(float amount) {
        rawDamageSentinel(amount);
    }

    public void rawDamageSentinel(float amount) {
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
        super.draw();

        float z = Draw.z();
        Draw.z(Layer.bullet);

        if (reinforcePhase < 2) {
            float progress = reinforcePhase == 0
                    ? (0.5f - healthf()) / 0.5f
                    : (0.2f - healthf()) / 0.2f;
            progress = Mathf.clamp(progress);

            Tmp.c1.set(team.color).lerp(Color.white, Mathf.absin(4f, 0.2f));
            Draw.color(Tmp.c1);
            Lines.stroke(3f);
            Lines.circle(x, y, hitSize * 1.3f);
            Lines.stroke(4f * progress);
            drawArc(x, y, hitSize * 1.3f, progress);
        }

        if (hitTime > 0.3f) {
            Draw.color(team.color, Color.white, hitTime * 0.5f);
            for (int i = 0; i < 3; i++) {
                float ang = pulseTimer * 3f + i * 120f;
                Tmp.v1.trns(ang, hitSize * 0.8f).add(x, y);
                Fill.circle(Tmp.v1.x, Tmp.v1.y, 3f * hitTime);
            }
        }

        if (reinforcePhase >= 2) {
            float pulse = Mathf.absin(pulseTimer, 8f, 1f);
            Draw.color(team.color, pulse * 0.4f);
            Fill.circle(x, y, hitSize * (1.1f + pulse * 0.15f));
        }

        Draw.z(z);
    }

    void drawArc(float cx, float cy, float radius, float fraction) {
        int sides = 60;
        int filled = (int)(sides * fraction);
        for (int i = 0; i < filled; i++) {
            float a1 = (float) i      / sides * Mathf.PI2;
            float a2 = (float)(i + 1) / sides * Mathf.PI2;
            Lines.line(
                cx + Mathf.cos(a1) * radius, cy + Mathf.sin(a1) * radius,
                cx + Mathf.cos(a2) * radius, cy + Mathf.sin(a2) * radius
            );
        }
    }

    @Override
    public void read(Reads read) {
        reinforcePhase = read.i();
        reinforceReload = read.f();
        recentDamage = read.f();
        super.read(read);
    }

    @Override
    public void write(Writes write) {
        write.i(reinforcePhase);
        write.f(reinforceReload);
        write.f(recentDamage);
        super.write(write);
    }

    @Override
    public void readSync(Reads read) {
        super.readSync(read);
        if (!isLocal()) {
            reinforcePhase = read.i();
            reinforceReload = read.f();
        } else {
            read.i(); read.f();
        }
    }

    @Override
    public void writeSync(Writes write) {
        super.writeSync(write);
        write.i(reinforcePhase);
        write.f(reinforceReload);
    }
}