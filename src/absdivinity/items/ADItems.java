package absdivinity.items;

import arc.graphics.Color;
import mindustry.type.Item;

public class ADItems {
    public static Item celestite, neutrolite, voidIron, abyssGlass, divinite;

    public static void load(){
        celestite = new Item("celestite", Color.valueOf("95c2ee")){{
            localizedName = "Целестит";
            description = "Легкий металл, который обладает высокой плотностью, но невероятно маленькой массой.";
            hardness = 4;
            cost = 2f;
        }};

        neutrolite = new Item("neutrolite", Color.valueOf("00aeff")){{
            localizedName = "Нейтролит";
            description = "Сгусток звездной энергии в кристаллической форме";
            hardness = 5;
            charge = 1.0f;
            cost = 3f;
        }};

        voidIron = new Item("void-iron", Color.valueOf("333a41")){{
            localizedName = "Железо Пустоши";
            description = "Темный металл, поглощающий свет. Остаток некой великой цивилизации...";
            hardness = 5;
            cost = 4f;
        }};

        abyssGlass = new Item("abyss-glass", Color.valueOf("5f5f5f")){{
            localizedName = "Стекло Бездны";
            description = "Бездна... Местные называли так пространство между мирами... Верили, что создав материал, они смогут увидеть других себя...";
            hardness = 6;
            explosiveness = 0.2f;
        }};
        divinite = new Item("divinite", Color.valueOf("bf92f9")){{
            localizedName = "Дивинит";
            description = "Они создали материал... Они увидели себя... Это их извратило... Лишило разума...";
            hardness = 10;
            radioactivity = 1.5f;
            charge = 2.0f;
            cost = 10f;
        }};
    }
}
