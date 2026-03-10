package absdivinity.bullets;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class BaHionBulletType extends ContinuousLaserBulletType {
    public Color[] colors = {
        Color.valueOf("95c2ee").cpy().a(0.5f),
        Color.valueOf("00aeff"),
        Color.valueOf("bf92f9").mul(1.2f),
        Color.white
    };

    public float creepTime = 100f;
    public float baseLength = 200f;

    public Effect mirrorFlash = new Effect(15f, e -> {
        Draw.color(colors[2].cpy().mul(1f + Mathf.random(0.5f, 1.5f)));
        Fill.poly(e.x, e.y, 3, 3f + e.fout() * 8f, Mathf.random(360f));
        Drawf.light(e.x, e.y, 30f * e.fout(), colors[2], 0.7f);
    }).layer(Layer.bullet + 0.1f);

    public Effect meltTrail = new Effect(800f, e -> {
        Draw.color(Color.orange, Color.red, e.fin());
        Fill.circle(e.x, e.y, 12f * e.fout());

        Draw.color(Color.yellow, Color.white, e.fin());
        Fill.circle(e.x, e.y, 6f * e.fout());

        if(Mathf.chanceDelta(0.15f)){
            Draw.color(colors[2]);
            Fill.square(e.x + Mathf.random(-8f, 8f), e.y + Mathf.random(-8f, 8f), 2f + e.fslope() * 3f, 45f);
            Drawf.light(e.x, e.y, 40f * e.fout(), colors[2], 0.5f);
        }

        Draw.color(Color.gray.cpy().a(0.4f * e.fout()));
        Fill.circle(e.x, e.y, 15f * e.fout());

        Drawf.light(e.x, e.y, 60f * e.fout(), Color.orange, 0.6f);
    }).layer(Layer.scorch);

    public BaHionBulletType() {
        damage = 600f;
        length = 1300f;
        width = 40f;
        lifetime = 300f;

        hitEffect = new Effect(60f, e -> {
            Draw.color(colors[2], Color.white, e.fin());
            Lines.stroke(5f * e.fout());
            Lines.circle(e.x, e.y, 80f * e.fin());

            Angles.randLenVectors(e.id, 30, 120f * e.fin(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 4f * e.fout());
                Drawf.light(e.x + x, e.y + y, 40f * e.fout(), colors[2], 0.7f);
            });

            Effect.shake(15f, 30f, e.x, e.y);
            Drawf.light(e.x, e.y, 200f, colors[1], 0.9f);
        });
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        float curLen = Mathf.lerp(baseLength, length, b.fin(Interp.pow3));
        float rot = b.rotation();

        if(Mathf.chanceDelta(0.25f)){
            float off = Mathf.random(curLen);
            Tmp.v2.trns(rot, off, Mathf.random(-width / 2f, width / 2f)).add(b.x, b.y);
            mirrorFlash.at(Tmp.v2.x, Tmp.v2.y, rot);
        }


        if(Mathf.chanceDelta(0.4f)){
            int segments = 18;
            float segmentLen = curLen / segments;
            for(int i = 1; i <= segments; i++){
                if(Mathf.chance(0.6f)){
                    Tmp.v2.trns(rot, i * segmentLen + Mathf.random(-8f, 8f), Mathf.random(-width / 3f, width / 3f)).add(b.x, b.y);
                    meltTrail.at(Tmp.v2.x, Tmp.v2.y, rot + Mathf.random(-15f, 15f));
                }
            }
        }

        if(b.time < creepTime){
            Damage.status(b.team, b.x, b.y, 10f, StatusEffects.melting, 60f, false, true);
        }
    }

    @Override
    public void draw(Bullet b){
        float curLen = Mathf.lerp(baseLength, length, b.fin(Interp.pow3));
        float rot = b.rotation();
        float pulse = 1f + Mathf.absin(Time.time, 6f, 0.35f);
        float fade = b.fout();

        for(int i = 0; i < colors.length; i++){
            float w = width * pulse * fade * (1f - (float)i / colors.length);
            Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time + i * 40f, 5f + i * 2f, 0.4f));

            Draw.color(Tmp.c1);
            Lines.stroke(w);
            Lines.lineAngle(b.x, b.y, rot, curLen);

            Fill.circle(b.x, b.y, w / 2f);
            Tmp.v1.trns(rot, curLen).add(b);
            Fill.circle(Tmp.v1.x, Tmp.v1.y, w * 1.2f * pulse);
        }

        Drawf.light(b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 2f * pulse, colors[1], 0.8f * fade);

        Draw.reset();
    }
}