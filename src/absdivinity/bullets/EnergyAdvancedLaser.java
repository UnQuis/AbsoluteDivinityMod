package absdivinity.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class EnergyAdvancedLaser extends ContinuousLaserBulletType {

    public EnergyAdvancedLaser(){
        damage    = 18f;
        length    = 180f;
        width     = 5f;
        lifetime  = 160f;
        fadeTime  = 25f;
        incendChance = 0.08f;

        shootEffect  = Fx.none;
        hitEffect    = Fx.hitLancer;

        colors = new Color[]{
            Color.valueOf("95c2ee").cpy().a(0.3f),
            Color.valueOf("00aeff").cpy().a(0.7f),
            Color.valueOf("bf92f9"),
            Color.white
        };
    }

    @Override
    public void draw(Bullet b){
        float fout    = b.fout(Interp.pow2Out);
        float realLen = length * b.fin(Interp.pow2Out);
        float rot     = b.rotation();
        float pulse   = 1f + Mathf.absin(Time.time, 3f, 0.12f);
        float baseW   = width * fout;

        Draw.z(Layer.groundUnit - 1f);
        float trailAlpha = fout * 0.45f;
        float trailW     = baseW * 2.2f;

        Draw.color(Color.valueOf("bf92f9").cpy().a(trailAlpha * 0.4f));
        Lines.stroke(trailW * 1.6f);
        Lines.lineAngle(b.x, b.y, rot, realLen * 0.92f);

        Draw.color(Color.valueOf("00aeff").cpy().a(trailAlpha * 0.7f));
        Lines.stroke(trailW * 0.5f);
        Lines.lineAngle(b.x, b.y, rot, realLen * 0.88f);

        Rand rand = new Rand(b.id + (long)(b.time / 4f));
        Draw.z(Layer.effect);
        for(int i = 0; i < 8; i++){
            float pos    = rand.random(realLen * 0.85f);
            float offY   = rand.range(trailW * 0.4f);
            Tmp.v1.trns(rot, pos, offY).add(b);
            float dotA   = trailAlpha * rand.random(0.3f, 0.8f);
            Draw.color(Color.valueOf("bf92f9").cpy().a(dotA));
            Fill.circle(Tmp.v1.x, Tmp.v1.y, rand.random(0.6f, 1.4f) * fout);
        }

        Draw.z(Layer.bullet);
        for(int i = 0; i < colors.length; i++){
            float layerW = baseW * (1f - (float)i / colors.length) * pulse;
            Draw.color(colors[i]);
            Lines.stroke(layerW);
            Lines.lineAngle(b.x, b.y, rot, realLen);

            Fill.circle(b.x, b.y, layerW / 1.4f);

            Tmp.v1.trns(rot, realLen).add(b);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, layerW / 1.4f);

            if(i == 1){
                Drawf.light(Tmp.v1.x, Tmp.v1.y, 28f * fout, colors[1], 0.7f);
            }
        }

        rand = new Rand(b.id);
        Draw.color(Color.white);
        for(int i = 0; i < 5; i++){
            float pos  = rand.random(realLen);
            float offY = rand.range(2f);
            Tmp.v1.trns(rot, pos, offY).add(b);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, 0.9f * fout);
        }

        Tmp.v1.trns(rot, realLen / 2f).add(b);
        Drawf.light(b.x, b.y, Tmp.v1.x, Tmp.v1.y, baseW * 4f, colors[1], 0.5f * fout);

        Draw.reset();
    }
}