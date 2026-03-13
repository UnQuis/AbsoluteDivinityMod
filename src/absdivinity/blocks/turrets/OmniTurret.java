package absdivinity.blocks.turrets;

import absdivinity.bullets.OmniChainBulletType;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.type.Category;
import mindustry.type.Item;
import static mindustry.type.ItemStack.with;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class OmniTurret extends ItemTurret {

    
    public float baseRange = 200f;
    
    public float maxRange  = 420f;
    
    public float rangePerStatPoint = 55f;

    public float maxBulletDrawSize  = 320f;
    public float maxSplashRadius    = 80f;

    public OmniTurret(String name) {
        super(name);

        size         = 2;
        health       = 1800;
        armor        = 5f;
        reload       = 18f;
        rotateSpeed  = 12f;
        shootCone    = 25f;
        inaccuracy   = 3f;
        targetAir    = true;
        targetGround = true;

        range = baseRange;

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
            OmniChainBulletType bullet = buildBullet(item);
            ammoTypes.put(item, bullet);
        });

        range = maxRange;
    }

    OmniChainBulletType buildBullet(Item item) {

        Color base;
        float f = item.flammability, c = item.charge, r = item.radioactivity;
        float total = f + c + r;

        if (total < 0.05f) {

            base = Color.valueOf("a0b8cc");
        } else if (f >= c && f >= r) {

            base = Color.valueOf("ff8c42").lerp(Color.valueOf("ff3010"), Mathf.clamp(f));
        } else if (c >= r) {

            base = Color.valueOf("42c5ff").lerp(Color.valueOf("bf92f9"), Mathf.clamp(c * 0.7f));
        } else {

            base = Color.valueOf("7aff42").lerp(Color.valueOf("d4ff00"), Mathf.clamp(r * 0.6f));
        }

        float dmg = 45f
            + item.explosiveness * 200f
            + item.charge        * 90f
            + item.radioactivity * 70f
            + item.hardness      * 5f;

        float statScore = item.explosiveness
                        + item.charge
                        + item.radioactivity
                        + Mathf.clamp(item.hardness / 15f);

        float bulletRange = Mathf.clamp(
            baseRange + statScore * rangePerStatPoint,
            baseRange,
            maxRange
        );

        OmniChainBulletType bullet = new OmniChainBulletType(dmg, item.charge, base, bulletRange, maxBulletDrawSize);

        if (item.radioactivity > 0.8f) {
            bullet.status         = StatusEffects.corroded;
            bullet.statusDuration = 60f * 8;
        } else if (item.radioactivity > 0.3f) {
            bullet.status         = StatusEffects.corroded;
            bullet.statusDuration = 60f * 3;
        }

        if (item.charge > 0.5f) {
            bullet.status         = StatusEffects.shocked;
            bullet.statusDuration = 60f * 2;
        }

        if (item.explosiveness > 0.5f) {
            bullet.splashDamage       = dmg * 0.45f;
            bullet.splashDamageRadius = Math.min(32f + item.explosiveness * 50f, maxSplashRadius);
        }

        return bullet;
    }
}