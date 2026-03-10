package absdivinity;

import arc.graphics.*;
import mindustry.type.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.content.*;
import absdivinity.bullets.EnergyAdvancedLaser;
import absdivinity.bullets.EnergyCreepLaserBulletType;
import absdivinity.bullets.EnergyEliteMissile;
import absdivinity.bullets.EnergyMissileBulletType;
import absdivinity.entities.units.EnergyDroneUnit;

public class EnergyCoreDrone extends UnitType {
    public EnergyCoreDrone(String name){
        super(name);

        constructor = () -> new EnergyDroneUnit();

        flying = true;
        health = 1200f;
        armor = 15f;
        speed = 6f;
        accel = 0.2f;
        drag = 0.1f;
        hitSize = 14f;
        engineSize = 3f;
        engineOffset = 7f;

        mineSpeed = 12f;
        mineTier = 3;
        buildSpeed = 15f;
        itemCapacity = 200;

        weapons.add(new Weapon("energy-laser") {{
            x = 6f;
            y = 2f;
            reload = 5f;
            continuous = true;
            mirror = true;
            bullet = new EnergyAdvancedLaser();
        }});

        weapons.add(new Weapon("energy-missile") {{
            x = 0f;
            y = -4f;
            reload = 70f;
            rotate = true;
            bullet = new EnergyEliteMissile();
        }});

        weapons.add(new Weapon("absolute-laser"){{
            x = 0f;
            y = -2f;
            reload = 240f;
            rotate = true;
            bullet = new EnergyCreepLaserBulletType();
        }});
    }
}