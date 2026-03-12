package absdivinity.blocks.turrets;

import absdivinity.bullets.OmniChainBulletType;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.type.Category;
import static mindustry.type.ItemStack.with;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class OmniTurret extends ItemTurret {

    public OmniTurret(String name) {
        super(name);

        size         = 2;
        health       = 1800;
        armor        = 5f;
        range        = 240f;
        reload       = 18f;
        rotateSpeed  = 12f;
        shootCone    = 25f;
        inaccuracy   = 3f;
        targetAir    = true;
        targetGround = true;

        requirements(Category.turret, with(
            Items.copper,   200,
            Items.lead,     150,
            Items.silicon,  100,
            Items.titanium,  50
        ));
    }

    @Override
    public void init() {
        super.init();

        Vars.content.items().each(item -> {
            Color base;
            if (item.flammability > item.charge && item.flammability > item.radioactivity) {
                base = Color.valueOf("ff8c42");
            } else if (item.charge > item.radioactivity && item.charge > 0.05f) {
                base = Color.valueOf("42c5ff").lerp(Color.valueOf("bf92f9"), item.charge * 0.6f);
            } else if (item.radioactivity > 0.1f) {
                base = Color.valueOf("7aff42").lerp(Color.valueOf("c8ff80"), item.radioactivity * 0.5f);
            } else {
                base = Color.valueOf("a0b8cc");
            }

            float dmg = 45f
                + item.explosiveness * 180f
                + item.charge       * 80f
                + item.radioactivity * 60f
                + item.hardness     * 4f;

            OmniChainBulletType bullet = new OmniChainBulletType(dmg, item.charge, base);

            if (item.radioactivity > 0.8f) {
                bullet.status         = StatusEffects.corroded;
                bullet.statusDuration = 60f * 6;
            } else if (item.radioactivity > 0.3f) {
                bullet.status         = StatusEffects.corroded;
                bullet.statusDuration = 60f * 2;
            }

            if (item.explosiveness > 0.5f) {
                bullet.splashDamage       = dmg * 0.4f;
                bullet.splashDamageRadius = 30f + item.explosiveness * 40f;
            }

            ammoTypes.put(item, bullet);
        });
    }
}