package absdivinity.bullets;

import arc.graphics.Color;
import arc.util.Time;
import arc.util.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;

public class EnergyMissileBulletType extends BasicBulletType {
    public EnergyMissileBulletType(float speed, float damage){
        super(speed, damage, "missile-large");
        width = 8f;
        height = 12f;
        shrinkY = 0f;
        homingPower = 0.8f;
        homingRange = 300;
        lifetime = 120f;
        trailColor = Pal.accent;
        backColor = Pal.accent;
        frontColor = Color.white;
        hitEffect = Fx.blastExplosion;
        despawnEffect = Fx.blastExplosion;
        weaveMag = 2f;
        weaveScale = 4f;
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.time > 10f){
            b.vel.rotate(Mathf.sin(b.time, weaveScale, weaveMag) * Time.delta);
        }
    } 
}
