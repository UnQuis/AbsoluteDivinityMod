package absdivinity.blocks.production;

import absdivinity.omni.UniversalDrillComp;
import mindustry.type.*;
import mindustry.content.*;
import mindustry.world.blocks.production.Drill;

public class RangeDrill extends UniversalDrillComp{
    public RangeDrill(String name){
        super(name);
        tier = 9;
        size = 5;
        health = 1300;
        drillTime = 20f;
        scanRange = 7;
        itemCapacity = 100;

        requirements(Category.production, ItemStack.with(
            Items.copper, 500,
            Items.silicon, 300,
            Items.plastanium, 150,
            Items.phaseFabric, 80,
            Items.surgeAlloy, 60
        ));

        hasPower = true;
        hasLiquids = true;

        consumePower(10f);
        consumeLiquid(Liquids.water, 0.7f).boost();

        liquidBoostIntensity = 3.1f;
    }
    
}
