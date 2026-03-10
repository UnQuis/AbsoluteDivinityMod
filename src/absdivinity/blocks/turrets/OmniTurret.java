package absdivinity.blocks.turrets;

import absdivinity.bullets.OmniChainBulletType;
import mindustry.*;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;

public class OmniTurret extends ItemTurret {
    public OmniTurret(String name) {
        super(name);
        health = 1500;
        range = 200f;
        reload = 20f;

        requirements(Category.defense, ItemStack.with(
                Items.copper, 200,
                Items.lead, 150,
                Items.silicon, 100,
                Items.titanium, 50
            ));
    }
    @Override
    public void init() {
        super.init();
        Vars.content.items().each(item ->{
            arc.graphics.Color bulletColor = arc.graphics.Color.white.cpy();

            if (item.flammability > item.charge && item.flammability > item.radioactivity) {
                bulletColor = arc.graphics.Color.orange;
            } else if (item.charge > item.radioactivity) {
                bulletColor = arc.graphics.Color.sky;
            }else if (item.radioactivity > 0.1f) {
                bulletColor = arc.graphics.Color.lime;
            }
            final  arc.graphics.Color finalColor = bulletColor;
            float finalDamage = 50f + (50f * item.explosiveness * 5);

            OmniChainBulletType bullet = new OmniChainBulletType(finalDamage, item.charge, finalColor);

            bullet.status = item.radioactivity > 0.5f ? StatusEffects.corroded : StatusEffects.none;
            bullet.statusDuration = 60f * 5;

            ammoTypes.put(item, bullet);
        });
    };
}