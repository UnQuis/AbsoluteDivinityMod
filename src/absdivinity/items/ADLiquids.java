package absdivinity.items;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class ADLiquids {
    public static Liquid aetherCoolant, ichor, abyssSludge;

    public static void load(){
        aetherCoolant = new Liquid("aether-coolant", Color.valueOf("42f5e6")){{
            localizedName = "Эфирный Хладагент";
            description = "Криогенная жидкость, насыщенная частицами Целестита.";
            heatCapacity = 1.2f;
            viscosity = 0.3f;
            temperature = 0f;
            coolant = true;
        }};

        ichor = new Liquid("ichor", Color.valueOf("ffd700")){{
            localizedName = "Ихор";
            description = "Они называли это 'Кровью Богов'. Они были близки к разгатке, но были... Испорчены...";
            heatCapacity = 0.4f;
            temperature = 0.6f;
            explosiveness = 0.5f;
            viscosity = 0.5f;
        }};

        abyssSludge = new Liquid("abyss-sludge", Color.valueOf("312e44")){{
            localizedName = "Материальная Бездна";
            description = "Они наткнулись на нее случайно... Решили использовать в своих целях... Она была слишком вязкой для их фабрик...";
            viscosity = 0.8f;
            flammability = 0.2f;
            heatCapacity = 0.1f;
        }};
    }
}
