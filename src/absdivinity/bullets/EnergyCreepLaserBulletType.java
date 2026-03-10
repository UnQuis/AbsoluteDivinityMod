package absdivinity.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class EnergyCreepLaserBulletType extends ContinuousLaserBulletType {
    public float creepTime  = 120f;
    public float baseLength = 50f;

    public Color[] colors = {
        Color.valueOf("95c2ee").cpy().a(0.35f),
        Color.valueOf("00aeff").cpy().a(0.8f),
        Color.valueOf("bf92f9"),
        Color.white
    };

    public EnergyCreepLaserBulletType() {
        super();
        damage    = 80f;
        length    = 320f;
        width     = 12f;
        hitColor  = Color.valueOf("00aeff");
        lifetime  = 300f;
        drawSize  = 450f;
    }

    public static class CreepData {
        public float lastX, lastY;
        public float[] burnX = new float[24];
        public float[] burnY = new float[24];
        public float[] burnA = new float[24];
        public int burnIdx   = 0;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        CreepData data = new CreepData();
        Vec2 v = Tmp.v1.trns(b.rotation(), baseLength).add(b.x, b.y);
        data.lastX = v.x;
        data.lastY = v.y;
        b.data = data;
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        if (!(b.data instanceof CreepData)) return;
        CreepData data = (CreepData)b.data;

        float curLen = calculateLength(b);
        Vec2 head    = Tmp.v1.trns(b.rotation(), curLen).add(b.x, b.y);

        if (Mathf.chanceDelta(0.4f)) {
            int idx = data.burnIdx % data.burnX.length;
            data.burnX[idx] = head.x + Mathf.range(width * 0.5f);
            data.burnY[idx] = head.y + Mathf.range(width * 0.5f);
            data.burnA[idx] = 1f;
            data.burnIdx++;
        }

        for (int i = 0; i < data.burnA.length; i++) {
            data.burnA[i] = Mathf.approachDelta(data.burnA[i], 0f, 1f / 90f);
        }

        float groundScl = 1f - Mathf.clamp((b.time - creepTime / 1.4f) / (creepTime / 4f));
        if (Mathf.chanceDelta(0.5f) && groundScl > 0) {
            Fx.ventSteam.at(
                head.x + Mathf.range(6f),
                head.y + Mathf.range(6f),
                b.rotation(), colors[1]
            );
        }

        data.lastX = head.x;
        data.lastY = head.y;
    }

    public float calculateLength(Bullet b) {
        return baseLength / Math.max(1f - Interp.sineIn.apply(Mathf.clamp(b.time / creepTime)), baseLength / length);
    }

    @Override
    public void draw(Bullet b) {
        if (!(b.data instanceof CreepData)) return;
        CreepData data = (CreepData)b.data;

        float curLen = calculateLength(b);
        float fade   = Mathf.clamp(b.time / 10f) * b.fout();
        float rot    = b.rotation();

        Draw.z(Layer.groundUnit - 1f);

        for (int i = 0; i < data.burnA.length; i++) {
            float a = data.burnA[i] * 0.6f;
            if (a < 0.01f) continue;

            Draw.color(Color.valueOf("bf92f9").cpy().a(a * 0.35f));
            Fill.circle(data.burnX[i], data.burnY[i], width * 1.2f);

            Draw.color(Color.valueOf("00aeff").cpy().a(a * 0.7f));
            Fill.circle(data.burnX[i], data.burnY[i], width * 0.5f);
        }

        float trailA = fade * 0.4f;
        Draw.color(Color.valueOf("bf92f9").cpy().a(trailA * 0.5f));
        Lines.stroke(width * 2.5f);
        Lines.lineAngle(b.x, b.y, rot, curLen * 0.9f);

        Draw.color(Color.valueOf("00aeff").cpy().a(trailA * 0.8f));
        Lines.stroke(width * 0.8f);
        Lines.lineAngle(b.x, b.y, rot, curLen * 0.86f);

        Draw.z(Layer.bullet);
        for (int i = 0; i < colors.length; i++) {
            float f = ((float)(colors.length - i) / colors.length);
            float w = f * (width + Mathf.absin(Time.time + i * 0.6f, 1.1f, width / 4f)) * fade;

            Draw.color(colors[i]);
            Lines.stroke(w);
            Lines.lineAngle(b.x, b.y, rot, curLen);

            Fill.circle(b.x, b.y, w / 1.5f);

            Vec2 tip = Tmp.v1.trns(rot, curLen);
            Fill.poly(b.x + tip.x, b.y + tip.y, 3, w * 1.2f, rot);
        }

        Draw.z(Layer.effect);
        Rand rand = new Rand(b.id);
        for (int i = 0; i < 18; i++) {
            float speed    = rand.random(8f, 16f);
            float progress = ((b.time + rand.random(speed)) / speed) % 1f;
            float l        = rand.random(12f, 45f);
            float off      = progress * curLen;
            Vec2 v         = Tmp.v1.trns(rot, off, rand.range(width / 3f)).add(b.x, b.y);

            Draw.color(rand.chance(0.5f) ? colors[1] : Color.white);
            Drawf.tri(v.x, v.y, width / 4f, l, rot);
        }

        Drawf.light(b.x, b.y, data.lastX, data.lastY, width * 4f, colors[1], 0.65f * fade);
        Draw.reset();
    }
}