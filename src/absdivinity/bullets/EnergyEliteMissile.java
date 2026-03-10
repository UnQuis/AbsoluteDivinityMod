package absdivinity.bullets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class EnergyEliteMissile extends BasicBulletType {
    public Color trailColor = Color.valueOf("bf92f9");

    public EnergyEliteMissile(){
        speed             = 5.5f;
        damage            = 65f;    // было 120f — урон снижен для баланса т1
        splashDamage      = 90f;    // было 200f
        splashDamageRadius = 45f;   // было 60f
        lifetime          = 100f;
        homingPower       = 0.15f;
        homingRange       = 320f;
        weaveMag          = 4f;
        weaveScale        = 8f;

        trailLength = 22;
        trailWidth  = 3.5f;
        trailColor  = Color.valueOf("bf92f9");

        hitEffect = despawnEffect = new Effect(55f, e -> {
            Draw.color(trailColor, Color.white, e.fin());

            // Взрывное кольцо
            Lines.stroke(3.5f * e.fout());
            Lines.circle(e.x, e.y, splashDamageRadius * e.fin());

            // Второе кольцо чуть меньше
            Draw.color(Color.valueOf("95c2ee"), Color.white, e.fin());
            Lines.stroke(2f * e.fout());
            Lines.circle(e.x, e.y, splashDamageRadius * 0.6f * e.fin());

            // Осколки
            Angles.randLenVectors(e.id, 28, splashDamageRadius * 1.3f * e.fin(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 2f * e.fout());
                Drawf.light(e.x + x, e.y + y, 16f * e.fout(), trailColor, 0.5f);
            });

            // Внутренняя вспышка
            Draw.color(Color.white, trailColor, e.fin());
            Fill.circle(e.x, e.y, splashDamageRadius * 0.25f * e.fout());

            Effect.shake(2.5f, 12f, e.x, e.y);
            Drawf.light(e.x, e.y, splashDamageRadius * 3.5f, trailColor, 0.85f);
        });

        trailEffect = new Effect(14f, e -> {
            float pulse = 1f + Mathf.absin(Time.time, 4f, 0.3f);
            Draw.color(trailColor.cpy().mul(pulse));
            Fill.circle(e.x, e.y, trailWidth * 1.6f * e.fslope());

            // Маленькое фиолетовое пятнышко на земле от выхлопа
            Draw.z(Layer.groundUnit - 1f);
            Draw.color(Color.valueOf("bf92f9").cpy().a(e.fout() * 0.3f));
            Fill.circle(e.x, e.y, trailWidth * 2.2f * e.fout());
            Draw.z(Layer.effect);
        });
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);

        // Яркое ядро ракеты
        Draw.color(trailColor, Color.white, b.fin() * 0.5f);
        Fill.circle(b.x, b.y, trailWidth * 2f * b.fin());

        // Пульсирующее свечение
        float pulse = 1f + Mathf.absin(Time.time, 3f, 0.2f);
        Drawf.light(b.x, b.y, trailWidth * 7f * pulse, trailColor, 0.65f);
    }

    @Override
    public void updateTrail(Bullet b){
        if(Mathf.chanceDelta(0.35f)){
            trailEffect.at(b.x, b.y, trailWidth, trailColor);
        }
    }
}