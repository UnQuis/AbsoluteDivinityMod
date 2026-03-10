package absdivinity.blocks.turrets;

import arc.graphics.*;
import arc.math.*;
import absdivinity.bullets.*;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;

import static mindustry.type.ItemStack.*;

public class BaHion extends PowerTurret{
    public BaHion(String name){
        super(name);

        health = 12000;
        size = 5;
        reload = 120f;
        range = 1300f;
        rotateSpeed = 5f;

        consumePower(180f / 60f);

        requirements(Category.turret, with(
            Items.surgeAlloy, 200,
            Items.phaseFabric, 100,
            Items.thorium, 150,
            Items.silicon, 250
        ));
        shootType = new BaHionBulletType();
    }
}
