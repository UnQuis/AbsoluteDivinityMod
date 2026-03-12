package absdivinity.blocks.turrets;

import absdivinity.bullets.BaHionBulletType;
import mindustry.content.Items;
import mindustry.type.Category;
import static mindustry.type.ItemStack.with;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class BaHion extends PowerTurret {

    public BaHion(String name) {
        super(name);

        size         = 5;
        health       = 14000;
        armor        = 18f;
        range        = 1300f;
        reload       = 115f;
        rotateSpeed  = 4.5f;
        shootCone    = 12f;
        targetAir    = true;
        targetGround = true;

        consumePower(190f / 60f);

        requirements(Category.turret, with(
            Items.surgeAlloy,  200,
            Items.phaseFabric, 100,
            Items.thorium,     150,
            Items.silicon,     250
        ));
        // TODO: swap to ADItems when confirmed:
        // requirements(Category.turret, with(
        //     ADItems.divinite,     80,
        //     ADItems.resonantAlloy, 60,
        //     ADItems.etherSteel,   100,
        //     ADItems.ancientCircuit, 40
        // ));

        shootType = new BaHionBulletType();
    }
}