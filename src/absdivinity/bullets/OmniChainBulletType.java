package absdivinity.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Trail;

public class OmniChainBulletType extends BulletType {

    public Color   bulletColor  = Color.white;
    public Color   glowColor;
    public int     maxChain     = 5;
    public float   chainRange   = 100f;
    public float   chainDamage  = 0f;

    public int   helixCount    = 3;
    public int   helixLength   = 28;
    public float helixWidth    = 2.2f;
    public float helixRadius   = 7f;
    public float helixSpeed    = 7f;

    public float accelFrom     = 0.15f;
    public float accelTo       = 0.7f;
    public float speedBegin    = 1.5f;

    protected static final Seq<Healthc> hitUnits = new Seq<>();

    public OmniChainBulletType(float damage, float charge, Color color) {
        this.damage      = damage;
        this.bulletColor = color.cpy();

        this.maxChain    = (int)(2 + charge * 14);
        this.chainRange  = 80f + charge * 130f;
        this.chainDamage = damage * (0.7f + charge * 0.3f);

        this.helixCount  = charge < 0.3f ? 2 : (charge < 0.7f ? 3 : 4);
        this.helixRadius = 5f + charge * 8f;
        this.helixWidth  = 1.5f + charge * 1.5f;

        this.speed    = 10f;
        this.speedBegin = 3f;
        this.lifetime = 22f;

        this.shootEffect   = Fx.none;
        this.hitEffect     = Fx.none;
        this.despawnEffect = Fx.none;
        this.trailEffect   = Fx.none;
        this.hittable      = true;
        this.collidesGround = true;
        this.collidesAir    = true;

        this.drawSize = (chainRange + helixRadius) * 2f;
    }

    @Override
    public void init() {
        super.init();
        glowColor = bulletColor.cpy().lerp(Color.white, 0.4f);
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        Trail[] trails = new Trail[helixCount];
        for (int i = 0; i < helixCount; i++) trails[i] = new Trail(helixLength);
        b.data = trails;
    }

    @Override
    public void update(Bullet b) {
        float accel = speedBegin + Interp.pow2Out.apply(
            Mathf.curve(b.fin(), accelFrom, accelTo)) * (speed - speedBegin);
        b.vel.setLength(accel);

        super.update(b);

        if (!(b.data instanceof Trail[])) return;
        Trail[] trails = (Trail[]) b.data;
        float phase = b.time * helixSpeed;
        for (int i = 0; i < helixCount; i++) {
            float ang = phase + i * (360f / helixCount);
            float ox  = Mathf.cosDeg(ang) * helixRadius;
            float oy  = Mathf.sinDeg(ang) * helixRadius;
            if (trails[i] == null) trails[i] = new Trail(helixLength);
            trails[i].length = helixLength;
            trails[i].update(b.x + ox, b.y + oy, b.fout());
        }
    }

    @Override
    public void removed(Bullet b) {
        super.removed(b);
        if (!(b.data instanceof Trail[])) return;
        Trail[] trails = (Trail[]) b.data;
        for (Trail t : trails) {
            if (t != null && t.size() > 0)
                Fx.trailFade.at(b.x, b.y, helixWidth, bulletColor, t.copy());
        }
    }

    @Override
    public void draw(Bullet b) {
        if (!(b.data instanceof Trail[])) return;
        Trail[] trails = (Trail[]) b.data;

        float fade  = b.fout();
        float pulse = 1f + Mathf.absin(Time.time, 4f, 0.25f);

        float z = Draw.z();
        Draw.z(z - 0.0001f);
        for (Trail t : trails) {
            if (t != null) t.draw(bulletColor, helixWidth * fade);
        }
        Draw.z(z);

        Draw.color(bulletColor.r, bulletColor.g, bulletColor.b, 0.2f * fade);
        Fill.circle(b.x, b.y, helixRadius * 1.6f * pulse);

        Draw.color(bulletColor, 0.55f * fade);
        Fill.circle(b.x, b.y, helixRadius * 0.9f * pulse);

        Draw.color(glowColor, fade);
        Fill.circle(b.x, b.y, helixRadius * 0.45f * pulse);

        float tri  = helixRadius * 0.85f * fade * pulse;
        float ang1 = b.time * helixSpeed * 0.6f;
        float ang2 = -b.time * helixSpeed * 0.5f + 60f;
        Draw.color(bulletColor, 0.8f * fade);
        Drawf.tri(b.x, b.y, tri * 0.4f, tri * 1.8f, ang1);
        Drawf.tri(b.x, b.y, tri * 0.4f, tri * 1.8f, ang1 + 180f);
        Draw.color(glowColor, 0.6f * fade);
        Drawf.tri(b.x, b.y, tri * 0.3f, tri * 1.2f, ang2);
        Drawf.tri(b.x, b.y, tri * 0.3f, tri * 1.2f, ang2 + 180f);

        // Light
        Drawf.light(b.x, b.y, helixRadius * 4f * pulse, bulletColor, 0.7f * fade);

        Draw.reset();
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);

        hitUnits.clear();
        float cx = x, cy = y;
        float dmg = chainDamage;

        for (int i = 0; i < maxChain; i++) {
            final float fcx = cx, fcy = cy;
            Teamc target = Units.closestEnemy(b.team, fcx, fcy, chainRange,
                u -> u instanceof Healthc && !hitUnits.contains((Healthc) u));

            if (target instanceof Healthc) {
                Healthc unit = (Healthc) target;
                spawnChain(fcx, fcy, unit.x(), unit.y(), dmg, i);
                unit.damage(dmg);
                hitUnits.add(unit);
                cx  = unit.x();
                cy  = unit.y();
                dmg *= 0.82f;
            } else break;
        }
    }

    void spawnChain(float x1, float y1, float x2, float y2, float dmg, int hopIndex) {
        final Color c = bulletColor;
        final float thick = helixWidth * (1f - hopIndex * 0.12f);

        final float jit = 18f + hopIndex * 8f;
        final float midX = (x1 + x2) / 2f + Mathf.range(jit);
        final float midY = (y1 + y2) / 2f + Mathf.range(jit);

        new Effect(28f, e -> {
            float progress = e.fin();

            Lines.stroke(thick * e.fout());
            Draw.color(c, e.fout() * 0.9f);
            int segs = 12;
            float lx = x1, ly = y1;
            for (int i = 1; i <= segs; i++) {
                float t  = i / (float) segs;
                float px = (1-t)*(1-t)*x1 + 2*(1-t)*t*midX + t*t*x2;
                float py = (1-t)*(1-t)*y1 + 2*(1-t)*t*midY + t*t*y2;
                Lines.line(lx, ly, px, py);
                lx = px; ly = py;
            }

            if (progress < 0.4f) {
                Draw.color(glowColor, e.fout());
                Angles.randLenVectors(e.id + hopIndex, 5, 20f * progress,
                    (ox, oy) -> Fill.circle(midX + ox, midY + oy, thick * 0.8f * e.fout()));
            }

            Drawf.light(x1, y1, x2, y2, thick * 3f, c, 0.5f * e.fout());
        }).at(x1, y1);
    }
}