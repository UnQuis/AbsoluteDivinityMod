package absdivinity.blocks;

import java.util.Locale;

import static mindustry.type.ItemStack.*;
import arc.graphics.Color;
import absdivinity.AbsDivinity;
import absdivinity.EnergyCoreDrone;
import absdivinity.entities.units.EnergyDroneUnit;
import mindustry.content.*;
import mindustry.content.Items;
import mindustry.type.*;
import mindustry.world.meta.BuildVisibility;
import mindustry.type.Category;
import mindustry.world.blocks.storage.CoreBlock;

public class EnergyCore extends  CoreBlock{
    public EnergyCore(String name){
        super(name);

        health = 70000;
        size = 6;
        armor = 8f;
        itemCapacity = 75000;
        unitCapModifier = 92;
        thrusterLength = 28f;

        hasPower = true;
        outputsPower = true;
        consumesPower = false;
        consumePower(0f);

        unitType = AbsDivinity.energyCoreDrone;

        requirements(Category.effect, with(
            Items.titanium, 8500,
            Items.thorium, 8000,
            Items.silicon, 10000,
            Items.copper, 15000,
            Items.surgeAlloy, 7000
        ));
    }
    public class EnergyCoreBuild extends CoreBuild {
        @Override
        public float getPowerProduction(){
            return 25f;
        }
    }
}
