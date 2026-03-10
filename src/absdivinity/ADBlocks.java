package absdivinity;

import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.content.*;
import mindustry.world.meta.Attribute;
import absdivinity.items.ADItems;
import arc.graphics.*;

public class ADBlocks {
    public static Block diviniteOre, oasisFloor, wastelandFloor;

    public static void load(){
        wastelandFloor = new Floor("wasteland-floor"){{
            localizedName = "Металлический Пол Пустоши";
            variants = 3;
        }};

        oasisFloor = new Floor("oasis-floor"){{
            localizedName = "Плиты Целестита";
            attributes.set(Attribute.heat, 0.5f);
        }};

        diviniteOre = new OreBlock("divinite-ore", ADItems.divinite){{
            localizedName = "Жила Дивинита";
        }};
    }
}
