package absdivinity.items;

import arc.graphics.Color;
import mindustry.type.Item;

public class ADItems {

    public static Item
        scrapAlloy,
        voidIron,
        celestite,
        frostShard,
        abyssGlass,
        ashCarbon,
        ruinDust;

    public static Item
        refinedScrap,
        celestAlloy,
        voidSteel,
        neutrolite,
        glassSteel,
        voidCrystal,
        frostedAlloy,
        carbonFilament;

    public static Item
        divinite,
        ancientCore,
        etherSteel,
        voidMatrix,
        crystalMatrix,
        resonantAlloy,
        ancientCircuit;

    public static Item
        abyssalShard,
        divineFrame,
        soulCrystal,
        verdictHeart,
        nullMatter,
        transcendCore;

    public static void load() {

        ruinDust = new Item("ruin-dust", Color.valueOf("6a6060")) {{
            localizedName = "Ruin Dust";
            description = "Pulverized remains of Verdict's structures. Covers everything. " +
                "Useless alone, but mixed with metals it becomes a decent binding agent.";
            hardness = 1;
            cost = 0.5f;
        }};

        scrapAlloy = new Item("scrap-alloy", Color.valueOf("4a5055")) {{
            localizedName = "Scrap Alloy";
            description = "Salvaged composite from Verdict's fallen cities. " +
                "Rough, inconsistent, but structurally intact. A starting point.";
            hardness = 2;
            cost = 1f;
        }};

        ashCarbon = new Item("ash-carbon", Color.valueOf("222428")) {{
            localizedName = "Ash Carbon";
            description = "Carbonized residue from Verdict's dead reactors. " +
                "Still faintly warm. The reactors ran for centuries after the civilization died.";
            hardness = 2;
            cost = 1f;
        }};

        celestite = new Item("celestite", Color.valueOf("95c2ee")) {{
            localizedName = "Celestite";
            description = "A lightweight metal of extraordinary density yet negligible mass. " +
                "Found near oasis formations — places where the planet still tries to live.";
            hardness = 4;
            cost = 2f;
        }};

        voidIron = new Item("void-iron", Color.valueOf("333a41")) {{
            localizedName = "Void Iron";
            description = "A dark alloy that devours light. " +
                "Remnants of a great civilization, scattered across the wastes. " +
                "They made a lot of it. They needed a lot of it.";
            hardness = 5;
            cost = 3f;
        }};

        frostShard = new Item("frost-shard", Color.valueOf("b8dff0")) {{
            localizedName = "Frost Shard";
            description = "A brittle crystalline mineral formed under glacial pressure. " +
                "Conducts cold energy with unusual efficiency. " +
                "The glacier keeps making them. The glacier does not stop.";
            hardness = 3;
            cost = 2f;
        }};

        abyssGlass = new Item("abyss-glass", Color.valueOf("5f5f5f")) {{
            localizedName = "Abyss Glass";
            description = "They called the space between worlds 'the Abyss'. " +
                "They believed that with this material, they could see other versions of themselves. " +
                "They were right.";
            hardness = 6;
            explosiveness = 0.2f;
            cost = 4f;
        }};

        refinedScrap = new Item("refined-scrap", Color.valueOf("5a6068")) {{
            localizedName = "Refined Scrap";
            description = "Scrap alloy re-smelted and purified. " +
                "It remembers the shapes it used to hold. We give it new ones.";
            hardness = 3;
            cost = 2f;
        }};

        celestAlloy = new Item("celest-alloy", Color.valueOf("7aafcc")) {{
            localizedName = "Celest Alloy";
            description = "Celestite fused with refined scrap lattices. " +
                "The lightness of the stars, anchored to the ruins of the dead.";
            hardness = 5;
            cost = 4f;
        }};

        voidSteel = new Item("void-steel", Color.valueOf("2a3038")) {{
            localizedName = "Void Steel";
            description = "Void iron carbonized with ash remnants under compression. " +
                "Denser than anything naturally occurring on Verdict. " +
                "The civilization used it as armor plating for their god-machines.";
            hardness = 7;
            cost = 5f;
        }};

        neutrolite = new Item("neutrolite", Color.valueOf("00aeff")) {{
            localizedName = "Neutrolite";
            description = "A condensed cluster of stellar energy in crystalline form. " +
                "Smelted from celestite under intense cold — the star remembers its origin.";
            hardness = 5;
            charge = 1.0f;
            cost = 5f;
        }};

        glassSteel = new Item("glass-steel", Color.valueOf("6a7078")) {{
            localizedName = "Glass Steel";
            description = "Abyss glass and void iron, fused under extreme pressure. " +
                "Brittle against blunt force, near-indestructible against energy weapons. " +
                "The civilization used it for observation ports into the Abyss.";
            hardness = 7;
            explosiveness = 0.1f;
            cost = 6f;
        }};

        voidCrystal = new Item("void-crystal", Color.valueOf("1a1e24")) {{
            localizedName = "Void Crystal";
            description = "A concentrated lens of void energy. " +
                "It does not reflect light — it consumes it. " +
                "It does not store energy — it *is* energy.";
            hardness = 8;
            radioactivity = 0.5f;
            cost = 7f;
        }};

        frostedAlloy = new Item("frosted-alloy", Color.valueOf("90c8d8")) {{
            localizedName = "Frosted Alloy";
            description = "Frost shards compressed into refined scrap at cryogenic temperatures. " +
                "Becomes brittle above 400K. Becomes impenetrable below 200K.";
            hardness = 6;
            cost = 5f;
        }};

        carbonFilament = new Item("carbon-filament", Color.valueOf("1a1a20")) {{
            localizedName = "Carbon Filament";
            description = "Ash carbon drawn into conductive strands under electromagnetic fields. " +
                "The civilization used these in their neural networks. " +
                "The filaments still carry signal. No one is sending.";
            hardness = 3;
            charge = 0.5f;
            cost = 4f;
        }};

        divinite = new Item("divinite", Color.valueOf("bf92f9")) {{
            localizedName = "Divinite";
            description = "They made the material. They saw themselves. " +
                "What they saw corrupted them — stripped them of reason, of self, of mercy. " +
                "We make it now. We think we are different.";
            hardness = 10;
            radioactivity = 1.5f;
            charge = 2.0f;
            cost = 10f;
        }};

        ancientCore = new Item("ancient-core", Color.valueOf("8a7060")) {{
            localizedName = "Ancient Core";
            description = "A recovered processing fragment from Verdict's civilization. " +
                "Still warm. Still running background processes. " +
                "Still afraid.";
            hardness = 9;
            cost = 9f;
        }};

        etherSteel = new Item("ether-steel", Color.valueOf("5a8090")) {{
            localizedName = "Ether Steel";
            description = "Structural metal tempered in void fields. " +
                "Used in the final generation of Verdict's war machines before the Collapse. " +
                "Before they stopped building machines and started building something else.";
            hardness = 8;
            cost = 8f;
        }};

        voidMatrix = new Item("void-matrix", Color.valueOf("0d0d18")) {{
            localizedName = "Void Matrix";
            description = "A lattice of structured void energy, stable only under divinite containment. " +
                "The Abyss made solid. The Abyss made *obedient*. " +
                "It has not accepted this.";
            hardness = 11;
            radioactivity = 1.0f;
            explosiveness = 0.3f;
            cost = 12f;
        }};

        crystalMatrix = new Item("crystal-matrix", Color.valueOf("60b8e0")) {{
            localizedName = "Crystal Matrix";
            description = "Neutrolite and frosted alloy woven into an energy-routing lattice. " +
                "Channels stellar and void energies simultaneously without destabilizing. " +
                "The civilization spent forty years learning to make this.";
            hardness = 9;
            charge = 1.5f;
            cost = 11f;
        }};

        resonantAlloy = new Item("resonant-alloy", Color.valueOf("90b8c8")) {{
            localizedName = "Resonant Alloy";
            description = "Celest alloy threaded with carbon filament under a divine energy field. " +
                "Vibrates at frequencies that destabilize void barriers. " +
                "Used in the shells of their god-machines.";
            hardness = 8;
            charge = 1.0f;
            cost = 10f;
        }};

        ancientCircuit = new Item("ancient-circuit", Color.valueOf("7a9870")) {{
            localizedName = "Ancient Circuit";
            description = "A fully restored processing array from the fallen civilization. " +
                "It runs their original code. We do not know what it does. " +
                "We use it anyway.";
            hardness = 10;
            charge = 1.5f;
            cost = 13f;
        }};

        abyssalShard = new Item("abyssal-shard", Color.valueOf("312e44")) {{
            localizedName = "Abyssal Shard";
            description = "A fragment of the Abyss itself — not a representation. Not a proxy. " +
                "The real thing. " +
                "It should not exist here. It should not exist *anywhere*.";
            hardness = 13;
            radioactivity = 2.0f;
            charge = 1.5f;
            explosiveness = 0.5f;
            cost = 18f;
        }};

        divineFrame = new Item("divine-frame", Color.valueOf("9a70c8")) {{
            localizedName = "Divine Frame";
            description = "A structural composite of void matrix and ether steel. " +
                "The skeleton of something that should not be built. " +
                "We build it anyway.";
            hardness = 12;
            radioactivity = 1.2f;
            cost = 16f;
        }};

        soulCrystal = new Item("soul-crystal", Color.valueOf("d0a8ff")) {{
            localizedName = "Soul Crystal";
            description = "Crystallized residue from Verdict's last moments. " +
                "The civilization did not die quietly. " +
                "This is what quiet deaths look like. This is what the other kind leaves behind.";
            hardness = 14;
            radioactivity = 1.8f;
            charge = 2.5f;
            cost = 20f;
        }};

        verdictHeart = new Item("verdict-heart", Color.valueOf("d4a8ff")) {{
            localizedName = "Heart of Verdict";
            description = "The last creation of the fallen civilization. " +
                "They poured everything into it — their knowledge, their grief, their final moments. " +
                "The planet crystallized it when they were gone. " +
                "It still beats.";
            hardness = 15;
            radioactivity = 2.5f;
            charge = 3.0f;
            cost = 25f;
        }};

        nullMatter = new Item("null-matter", Color.valueOf("080810")) {{
            localizedName = "Null Matter";
            description = "Anti-material harvested from the boundary between the Abyss and real space. " +
                "It exists by not existing. " +
                "Our instruments cannot measure it. " +
                "Our instruments are afraid of it.";
            hardness = 16;
            radioactivity = 3.0f;
            explosiveness = 1.0f;
            cost = 30f;
        }};

        transcendCore = new Item("transcend-core", Color.valueOf("e8d0ff")) {{
            localizedName = "Transcendence Core";
            description = "Beyond divinite. Beyond the civilization. Beyond the Abyss. " +
                "The civilization never made this. " +
                "No one has made this before. " +
                "We are the first. " +
                "We will also be the last.";
            hardness = 20;
            radioactivity = 4.0f;
            charge = 5.0f;
            explosiveness = 0.8f;
            cost = 50f;
        }};
    }
}