package absdivinity.blocks.production;

import mindustry.*;
import mindustry.world.blocks.production.Drill;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;

public class RayDrill extends Drill{
    public RayDrill(String name){
        super(name);
        health = 500;
        drillTime = 30f;
        tier = 9;
        size = 4;

        requirements(Category.production, ItemStack.with(
            Items.copper, 200,
            Items.titanium, 150,
            Items.silicon, 120,
            Items.graphite, 100,
            Items.thorium, 80
        ));
            hasPower = true;
            hasLiquids = true;

            consumePower(5.4f);
            consumeLiquid(Liquids.water, 0.5f).boost();

            liquidBoostIntensity = 4.3f;
    }
    
}
