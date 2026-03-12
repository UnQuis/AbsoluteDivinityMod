package absdivinity.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class BaHionBulletType extends ContinuousLaserBulletType {

    public Color[] colors = {
        Color.valueOf("95c2ee").cpy().a(0.45f),
        Color.valueOf("00aeff"),
        Color.valueOf("bf92f9"),
        Color.white
    };

    public float creepTime       = 80f;
    public float baseLength      = 160f;
    public float lightningInterval = 8f;

    public Effect mirrorFlash = new Effect(18f, e -> {
        Color c = colors[2];
        Draw.color(c.r, c.g, c.b, e.fout() * 0.9f);
        Fill.poly(e.x, e.y, 3, 3f + e.fout() * 9f, e.rotation);
        Fill.poly(e.x, e.y, 3, 2f + e.fout() * 5f, e.rotation + 60f);
        Drawf.light(e.x, e.y, 28f * e.fout(), c, 0.6f);
    }).layer(Layer.bullet + 0.1f);

    public Effect plasmaParticle = new Effect(25f, e -> {
        Draw.color(colors[1], colors[3], e.fin());
        float sz = (3f + e.fslope() * 6f) * e.fout();
        Drawf.tri(e.x, e.y, sz * 0.6f, sz * 2.5f, e.rotation);
        Drawf.tri(e.x, e.y, sz * 0.6f, sz * 2.5f, e.rotation + 180f);
        Drawf.light(e.x, e.y, sz * 6f, colors[1], 0.5f);
    }).layer(Layer.bullet);

    public Effect groundMelt = new Effect(700f, e -> {
        Draw.color(Color.orange, Color.scarlet, e.fin());
        Fill.circle(e.x, e.y, 10f * e.fout());
        Draw.color(Color.yellow, Color.white, e.fin());
        Fill.circle(e.x, e.y, 5f * e.fout());
        if (Mathf.chanceDelta(0.12f)) {
            Draw.color(colors[2], colors[1], e.fslope());
            Fill.square(e.x + Mathf.range(7f), e.y + Mathf.range(7f),
                        2f + e.fslope() * 3f, 45f);
        }
        Draw.color(Color.gray.r, Color.gray.g, Color.gray.b, 0.35f * e.fout());
        Fill.circle(e.x, e.y, 14f * e.fout());
        Drawf.light(e.x, e.y, 55f * e.fout(), Color.orange, 0.55f);
    }).layer(Layer.scorch);

    public BaHionBulletType() {
        damage    = 680f;
        length    = 1300f;
        width     = 44f;
        lifetime  = 300f;
        drawSize  = 1500f;

        hitColor  = colors[1];

        hitEffect = new Effect(70f, e -> {
            Draw.color(colors[2], Color.white, e.fin());
            Lines.stroke(5f * e.fout());
            Lines.circle(e.x, e.y, 90f * e.fin());
            Angles.randLenVectors(e.id, 28, 130f * e.fin(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 4f * e.fout());
                Drawf.light(e.x + x, e.y + y, 35f * e.fout(), colors[2], 0.7f);
            });
            Draw.color(Color.white, colors[1], e.fin());
            Fill.circle(e.x, e.y, 30f * e.fout() * Mathf.curve(e.fout(), 0f, 0.4f));
            Effect.shake(14f, 28f, e.x, e.y);
            Drawf.light(e.x, e.y, 220f, colors[1], 0.9f);
        });
    }

    float curLen(Bullet b) {
        return Mathf.lerp(baseLength, length, Interp.pow3Out.apply(Mathf.clamp(b.time / creepTime)));
    }

    @Override
    public void update(Bullet b) {
        super.update(b);

        float cl  = curLen(b);
        float rot = b.rotation();

        if (Mathf.chanceDelta(0.28f)) {
            Tmp.v2.trns(rot, Mathf.random(cl), Mathf.range(width * 0.45f)).add(b.x, b.y);
            mirrorFlash.at(Tmp.v2.x, Tmp.v2.y, rot + Mathf.range(30f));
        }

        if (Mathf.chanceDelta(0.5f)) {
            int segs = 20;
            for (int i = 1; i <= segs; i++) {
                if (Mathf.chance(0.5f)) {
                    Tmp.v2.trns(rot, i * (cl / segs) + Mathf.range(10f),
                                Mathf.range(width * 0.4f)).add(b.x, b.y);
                    plasmaParticle.at(Tmp.v2.x, Tmp.v2.y, rot + Mathf.range(20f));
                }
            }
        }

        if (Mathf.chanceDelta(0.35f)) {
            Tmp.v2.trns(rot, cl + Mathf.range(12f), Mathf.range(width * 0.3f)).add(b.x, b.y);
            groundMelt.at(Tmp.v2.x, Tmp.v2.y);
        }

        if (b.timer(1, lightningInterval)) {
            int bolts = 3 + (int)(b.fin() * 4);
            for (int i = 0; i < bolts; i++) {
                float off = Mathf.random(cl * 0.3f, cl * 0.95f);
                Tmp.v2.trns(rot, off, Mathf.range(width * 0.5f)).add(b.x, b.y);
                Lightning.create(b.team, colors[1], damage * 0.08f,
                                 Tmp.v2.x, Tmp.v2.y,
                                 rot + Mathf.range(55f), 8 + Mathf.random(8));
            }
        }

        if (b.time < creepTime) {
            Damage.status(b.team, b.x, b.y, 14f, StatusEffects.melting, 60f, false, true);
        }
    }

    @Override
    public void draw(Bullet b) {
        float cl   = curLen(b);
        float rot  = b.rotation();
        float fade = b.fout(Interp.pow2In);
        float pulse = 1f + Mathf.absin(Time.time, 5f, 0.3f);

        Tmp.v1.trns(rot, cl).add(b);

        for (int i = 0; i < colors.length; i++) {
            float frac = 1f - (float) i / colors.length;
            float w    = width * pulse * fade * frac;
            Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time + i * 45f, 4f + i * 2f, 0.35f));
            Draw.color(Tmp.c1);
            Lines.stroke(w);
            Lines.lineAngle(b.x, b.y, rot, cl);
            Fill.circle(b.x, b.y, w * 0.5f);
        }

        Lines.stroke(1.2f * fade);
        Draw.color(colors[1], colors[2], 0.5f + Mathf.absin(Time.time, 3f, 0.5f));
        for (int i = 0; i < 12; i++) {
            float hash1 = Mathf.sin((b.id * 7.3f + i * 31.4f)) * 0.5f + 0.5f;
            float hash2 = Mathf.sin((b.id * 13.1f + i * 17.7f)) * 0.5f + 0.5f;
            float hash3 = Mathf.sin((b.id * 5.9f  + i * 41.2f));
            float t1   = 0.05f + hash1 * 0.4f;
            float t2   = t1   + 0.05f + hash2 * 0.2f;
            float side = (hash3 > 0 ? 1 : -1) * (width * 0.5f + hash2 * 4f);
            float sx1 = b.x + Mathf.cosDeg(rot) * t1 * cl - Mathf.sinDeg(rot) * side;
            float sy1 = b.y + Mathf.sinDeg(rot) * t1 * cl + Mathf.cosDeg(rot) * side;
            float sx2 = b.x + Mathf.cosDeg(rot) * t2 * cl - Mathf.sinDeg(rot) * side;
            float sy2 = b.y + Mathf.sinDeg(rot) * t2 * cl + Mathf.cosDeg(rot) * side;
            Lines.line(sx1, sy1, sx2, sy2);
        }

        float tipSize = width * 1.4f * pulse * fade;
        Draw.color(Color.white, colors[1], 0.4f + Mathf.absin(Time.time, 3f, 0.4f));
        Drawf.tri(Tmp.v1.x, Tmp.v1.y, tipSize * 0.5f, tipSize * 2.2f, rot);
        Draw.color(colors[2]);
        Drawf.tri(Tmp.v1.x, Tmp.v1.y, tipSize * 0.3f, tipSize * 1.4f, rot + 120f);
        Drawf.tri(Tmp.v1.x, Tmp.v1.y, tipSize * 0.3f, tipSize * 1.4f, rot + 240f);

        Draw.color(colors[3], fade * (0.6f + Mathf.absin(Time.time, 4f, 0.3f)));
        Fill.circle(b.x, b.y, width * 0.4f * pulse * fade);
        Draw.color(colors[1], fade * 0.5f);
        Fill.circle(b.x, b.y, width * 0.7f * pulse * fade);

        Drawf.light(b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 2.8f * pulse, colors[1], 0.85f * fade);
        Drawf.light(Tmp.v1.x, Tmp.v1.y, tipSize * 5f, Color.white, 0.6f * fade);

        Draw.reset();
    }
}