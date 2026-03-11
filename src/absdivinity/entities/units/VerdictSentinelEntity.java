package absdivinity.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import absdivinity.content.ADUnits;

public class VerdictSentinelEntity extends UnitEntity {

    public static final float MAX_DPS     = 60000f;
    public static final float DPS_RESUME  = MAX_DPS / 60f;
    public static final float MAX_ONCE    = 8000f;
    public float recentDamage = MAX_DPS;

    public int phase = 0;
    public float phaseImmunityTimer = 0f;
    public static final float IMMUNITY_DUR = 120f;
    public static final float PHASE2_THRESHOLD = 0.65f;
    public static final float PHASE3_THRESHOLD = 0.30f;

    public static final int CLAMP_COUNT = 12;
    protected Clamper[] clamps;

    public float lightningCooldown = 0;
    public static final float LIGHTNING_INTERVAL = 15f;
    public static final float LIGHTNING_RANGE    = 280f;
    public static final float LIGHTNING_DMG      = 220f;

    public static final float BASE_SHIELD = 15000f;
    public static final float SHIELD_REGEN = 20f;

    protected Trail[] orbTrails = new Trail[6];
    public float orbRot = 0;

    public float pulse = 0;
    protected Interval timer = new Interval(3);
    protected static final Rand rand = new Rand();

    public static int _classId = 0;
    @Override public int classId() { return _classId; }
    @Override public float mass()   { return 10_000_000f; }

    @Override
    public void setType(UnitType type) {
        super.setType(type);
        shield = BASE_SHIELD;

        clamps = new Clamper[CLAMP_COUNT];
        for (int i = 0; i < CLAMP_COUNT; i++) clamps[i] = new Clamper(i);

        for (int i = 0; i < orbTrails.length; i++)
            orbTrails[i] = new Trail(22);
    }

    @Override
    public void update() {
        super.update();

        pulse += Time.delta;
        orbRot += Time.delta * (1.4f + phase * 0.55f);
        lightningCooldown -= Time.delta;
        phaseImmunityTimer -= Time.delta;

        recentDamage = Math.min(recentDamage + DPS_RESUME * Time.delta, MAX_DPS);

        if (recentDamage > MAX_DPS * 0.6f && shield < BASE_SHIELD) {
            shield = Math.min(shield + SHIELD_REGEN * Time.delta, BASE_SHIELD);
            shieldAlpha = Math.min(shieldAlpha + 0.015f, 1f);
        }

        if (phase == 0 && healthf() < PHASE2_THRESHOLD) enterPhase(1);
        else if (phase == 1 && healthf() < PHASE3_THRESHOLD) enterPhase(2);

        Unit target = Units.closestEnemy(team, x, y, 700, u -> true);

        for (Clamper c : clamps) {
            c.target = target;
            c.update();
        }

        if (hitTime > 0.6f && lightningCooldown <= 0) {
            lightningCooldown = LIGHTNING_INTERVAL / (1 + phase);
            int bolts = 4 + phase * 4; // 4 / 8 / 12
            for (int i = 0; i < bolts; i++) {
                Lightning.create(team, team.color, LIGHTNING_DMG,
                        x, y, Mathf.random(360f), Mathf.random(10, 24));
            }
        }

        for (int i = 0; i < orbTrails.length; i++) {
            float ang  = orbRot + i * 60f;
            float rad  = hitSize * (1.35f + (i % 2) * 0.22f);
            Tmp.v1.trns(ang, rad).add(x, y);
            orbTrails[i].update(Tmp.v1.x, Tmp.v1.y, 1f);
        }
    }

    void enterPhase(int newPhase) {
        phase = newPhase;
        phaseImmunityTimer = IMMUNITY_DUR;
        shieldAlpha = 1f;
        shield = Math.min(shield + BASE_SHIELD * 0.25f, BASE_SHIELD);

        for (int i = 0; i < 8; i++)
            Lightning.create(team, team.color, LIGHTNING_DMG * 2,
                    x, y, i * 45f, Mathf.random(18, 30));

        Fx.unitShieldBreak.at(x, y, 0f, team.color, this);

        int count = newPhase == 1 ? 4 : 8;
        UnitType minion = newPhase == 1 ? ADUnits.diviniteShield : ADUnits.corruptedTitan;
        for (int i = 0; i < count; i++) {
            float angle = (360f / count) * i;
            Tmp.v1.trns(angle, hitSize * 3f).add(x, y);
            minion.spawn(team, Tmp.v1.x, Tmp.v1.y);
        }
    }

    @Override
    public void damage(float amount) {
        if (phaseImmunityTimer > 0) return;
        sentinelRawDamage(amount);
    }

    void sentinelRawDamage(float amount) {
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
        super.draw();

        float z = Draw.z();
        Color col = phaseColor();

        Draw.z(Layer.bullet);
        for (int i = 0; i < orbTrails.length; i++) {
            float alpha = 0.55f + Mathf.absin(pulse + i * 30f, 5f, 0.3f);
            Tmp.c1.set(col).a(alpha);
            orbTrails[i].draw(Tmp.c1, hitSize * 0.11f + i * 0.3f);
            orbTrails[i].drawCap(Tmp.c1, hitSize * 0.11f + i * 0.3f);
        }

        if (phaseImmunityTimer > 0) {
            float p = Interp.pow3Out.apply(phaseImmunityTimer / IMMUNITY_DUR);
            Draw.z(Layer.effect);
            Draw.color(Color.white, col, 1f - p * 0.6f);
            Draw.alpha(p * 0.75f);
            Fill.circle(x, y, hitSize * (1f + p * 0.35f));
        }

        Draw.z(Layer.bullet);
        Draw.color(col, 0.8f + Mathf.absin(pulse, 4f, 0.15f));
        Lines.stroke(4f);
        float thresh = phase == 0 ? PHASE2_THRESHOLD : PHASE3_THRESHOLD;
        if (phase < 2) {
            float progress = phase == 0
                    ? Mathf.clamp(1f - (healthf() - PHASE2_THRESHOLD) / (1f - PHASE2_THRESHOLD))
                    : Mathf.clamp(1f - (healthf() - PHASE3_THRESHOLD) / (PHASE2_THRESHOLD - PHASE3_THRESHOLD));
            drawArc(hitSize * 1.5f, progress);
        }

        Draw.z(Layer.effect - 1f);
        Lines.stroke(2.5f);
        Draw.color(col, 0.6f + Mathf.absin(pulse, 3f, 0.25f));
        for (Clamper c : clamps) c.draw();

        if (hitTime > 0.3f) {
            Draw.z(Layer.bullet + 1f);
            rand.setSeed(id);
            for (int i = 0; i < 5; i++) {
                float ang = rand.random(360f) + pulse * 2;
                float r   = hitSize * rand.random(0.3f, 0.85f);
                Tmp.v1.trns(ang, r).add(x, y);
                Draw.color(col, Color.white, hitTime * 0.5f);
                Fill.circle(Tmp.v1.x, Tmp.v1.y, 3.5f * hitTime);
            }
        }

        Draw.z(z);
    }

    Color phaseColor() {
        if (phase == 0) return team.color;
        if (phase == 1) return Tmp.c1.set(team.color).lerp(Color.valueOf("ff8c00"), 0.45f).cpy();
        return             Tmp.c1.set(Color.valueOf("ff2222")).lerp(team.color, 0.25f).cpy();
    }

    void drawArc(float radius, float fraction) {
        int segs = 80;
        int filled = (int)(segs * Mathf.clamp(fraction));
        for (int i = 0; i < filled; i++) {
            float a1 = (float)i       / segs * Mathf.PI2;
            float a2 = (float)(i + 1) / segs * Mathf.PI2;
            Lines.line(
                x + Mathf.cos(a1) * radius, y + Mathf.sin(a1) * radius,
                x + Mathf.cos(a2) * radius, y + Mathf.sin(a2) * radius
            );
        }
    }

    class Clamper {
        Unit target;
        float cx, cy, strength;
        final int index;

        Clamper(int i) {
            this.index = i;
            float ang = i * (360f / CLAMP_COUNT);
            cx = x + Mathf.cosDeg(ang) * hitSize;
            cy = y + Mathf.sinDeg(ang) * hitSize;
            strength = 0.6f + (i % 3) * 0.25f;
        }

        void update() {
            if (target != null && target.isAdded() && target.team != team) {
                cx = target.x;
                cy = target.y;
                Tmp.v1.set(x, y).sub(cx, cy).setLength(strength * (4f + phase * 3f));
                target.impulseNet(Tmp.v1);

                if (Mathf.chanceDelta(0.05f))
                    target.damage(8f * (1 + phase));
            } else {
                float defaultAng = orbRot * 0.5f + index * (360f / CLAMP_COUNT);
                float tx = x + Mathf.cosDeg(defaultAng) * hitSize * 1.1f;
                float ty = y + Mathf.sinDeg(defaultAng) * hitSize * 1.1f;
                cx = Mathf.lerpDelta(cx, tx, 0.025f);
                cy = Mathf.lerpDelta(cy, ty, 0.025f);
            }
        }

        void draw() {
            float dst = Mathf.dst(cx, cy, x, y);
            if (dst < 2f) return;
            float ang = Mathf.angle(cx - x, cy - y);
            Tmp.v1.set(cx, cy).sub(x, y).scl(0.33f);
            float len = dst;
            Tmp.v2.trns(ang + 90, strength * len / 4).add(Tmp.v1);
            Tmp.v3.trns(ang + 90, strength * len / 8).add(Tmp.v1.scl(2f));
            Lines.curve(x, y,
                x + Tmp.v2.x, y + Tmp.v2.y,
                x + Tmp.v3.x, y + Tmp.v3.y,
                cx, cy, Math.max(4, (int)(len / 14)));
        }
    }

    @Override public void read(Reads r) { phase = r.i(); recentDamage = r.f(); super.read(r); }
    @Override public void write(Writes w) { w.i(phase); w.f(recentDamage); super.write(w); }

    @Override
    public void readSync(Reads r) {
        super.readSync(r);
        if (!isLocal()) { phase = r.i(); }
        else r.i();
    }
    @Override
    public void writeSync(Writes w) { super.writeSync(w); w.i(phase); }
}