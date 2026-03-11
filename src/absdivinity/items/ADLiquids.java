package absdivinity.items;

import arc.graphics.Color;
import mindustry.type.Liquid;

public class ADLiquids {

    public static Liquid
        glacierMelt,
        voidSeep,
        ashSteam;

    public static Liquid
        aetherCoolant,
        ichor,
        abyssSludge,
        carbonSolution;

    public static Liquid
        refinedIchor,
        voidEssence,
        cryoflux,
        resonantFluid;

    public static Liquid
        divineFlux,
        etherPlasma,
        abyssalTide,
        voidBlood;

    public static void load() {

        glacierMelt = new Liquid("glacier-melt", Color.valueOf("c8e8f5")) {{
            localizedName = "Glacier Melt";
            description = "Runoff from Verdict's glacial regions, carrying trace minerals. " +
                "Impure, but available in bulk. The glacier does not care what we do with it.";
            heatCapacity = 0.9f;
            viscosity = 0.2f;
            temperature = 0.1f;
            coolant = true;
        }};

        voidSeep = new Liquid("void-seep", Color.valueOf("1a1020")) {{
            localizedName = "Void Seep";
            description = "A dark fluid that bleeds from corrupted ground. " +
                "The locals stopped naming it after the third settlement disappeared. " +
                "We name it anyway. We are not locals.";
            heatCapacity = 0.3f;
            viscosity = 0.6f;
            temperature = 0.4f;
            flammability = 0.15f;
        }};

        ashSteam = new Liquid("ash-steam", Color.valueOf("909090")) {{
            localizedName = "Ash Steam";
            description = "Steam from Verdict's still-active reactor vents. " +
                "Carries carbon particulates and trace radiation. " +
                "The reactors have been running for three hundred years without maintenance. " +
                "We do not know what they are running.";
            heatCapacity = 0.5f;
            viscosity = 0.1f;
            temperature = 0.65f;
            flammability = 0.05f;
        }};

        aetherCoolant = new Liquid("aether-coolant", Color.valueOf("42f5e6")) {{
            localizedName = "Aether Coolant";
            description = "A cryogenic fluid saturated with celestite microparticles. " +
                "Cools reactors far beyond the limits of ordinary water. " +
                "The civilization used it to keep the Abyss-looking machines from overheating.";
            heatCapacity = 1.2f;
            viscosity = 0.3f;
            temperature = 0f;
            coolant = true;
        }};

        ichor = new Liquid("ichor", Color.valueOf("ffd700")) {{
            localizedName = "Ichor";
            description = "They called it the Blood of Gods. " +
                "They were close to understanding why. " +
                "Then they were changed, and they stopped needing to understand.";
            heatCapacity = 0.4f;
            temperature = 0.6f;
            explosiveness = 0.5f;
            viscosity = 0.5f;
        }};

        abyssSludge = new Liquid("abyss-sludge", Color.valueOf("312e44")) {{
            localizedName = "Abyss Sludge";
            description = "They found it by accident. Used it in their factories. " +
                "It was too viscous — it clogged their machines. " +
                "The machines kept running anyway. Faster, actually.";
            viscosity = 0.8f;
            flammability = 0.2f;
            heatCapacity = 0.1f;
        }};

        carbonSolution = new Liquid("carbon-solution", Color.valueOf("303030")) {{
            localizedName = "Carbon Solution";
            description = "Ash carbon dissolved in void seep under heat. " +
                "Highly conductive. The civilization used it in their neural relay systems. " +
                "We use it in our factories. It seems fine with this.";
            heatCapacity = 0.6f;
            viscosity = 0.4f;
            temperature = 0.5f;
        }};

        refinedIchor = new Liquid("refined-ichor", Color.valueOf("e8c000")) {{
            localizedName = "Refined Ichor";
            description = "Stabilized ichor with the most volatile components removed. " +
                "Still burns. Still corrupts. Still remembers what it used to be. " +
                "But now it can be *used*.";
            heatCapacity = 0.55f;
            temperature = 0.7f;
            explosiveness = 0.2f;
            viscosity = 0.4f;
        }};

        voidEssence = new Liquid("void-essence", Color.valueOf("0d0818")) {{
            localizedName = "Void Essence";
            description = "The Abyss concentrated into fluid form through divinite lattice containment. " +
                "It does not flow. It *decides* where to go. " +
                "We have learned to ask politely.";
            heatCapacity = 0.8f;
            viscosity = 0.35f;
            temperature = 0.85f;
            flammability = 0.4f;
            explosiveness = 0.3f;
        }};

        cryoflux = new Liquid("cryoflux", Color.valueOf("a0e8ff")) {{
            localizedName = "Cryoflux";
            description = "Neutrolite suspended in ultra-cold aether coolant. " +
                "Approaches absolute zero at output. " +
                "Do not touch. Do not store near living tissue. " +
                "Do not ask what happened to the last lab.";
            heatCapacity = 1.6f;
            viscosity = 0.25f;
            temperature = -0.2f;
            coolant = true;
        }};

        resonantFluid = new Liquid("resonant-fluid", Color.valueOf("80a8c0")) {{
            localizedName = "Resonant Fluid";
            description = "Resonant alloy in colloidal suspension. " +
                "Amplifies energy weapon output when used as a catalyst. " +
                "The civilization called it 'the voice of the machines'. " +
                "The machines still use it to speak.";
            heatCapacity = 0.7f;
            viscosity = 0.5f;
            temperature = 0.45f;
        }};

        divineFlux = new Liquid("divine-flux", Color.valueOf("c090f8")) {{
            localizedName = "Divine Flux";
            description = "A suspension of divinite particles in aether coolant. " +
                "Required for the final stages of divine-tier construction. " +
                "Do not ingest. Do not inhale. Do not *think about it too directly*.";
            heatCapacity = 1.0f;
            viscosity = 0.45f;
            temperature = 0.3f;
            coolant = true;
        }};

        etherPlasma = new Liquid("ether-plasma", Color.valueOf("80c8ff")) {{
            localizedName = "Ether Plasma";
            description = "Superheated stellar energy in liquid state, " +
                "stabilized through a neutrolite containment field. " +
                "Required for the construction of apex war machines. " +
                "The containment field holds. Probably.";
            heatCapacity = 0.6f;
            viscosity = 0.15f;
            temperature = 0.95f;
            flammability = 0.1f;
        }};

        abyssalTide = new Liquid("abyssal-tide", Color.valueOf("180f2a")) {{
            localizedName = "Abyssal Tide";
            description = "Liquid Abyss, held in null matter containment. " +
                "It is aware of being contained. It does not appreciate it. " +
                "Our instruments have begun writing messages to each other at night. " +
                "We do not know what they are saying.";
            heatCapacity = 1.2f;
            viscosity = 0.6f;
            temperature = 0.9f;
            flammability = 0.6f;
            explosiveness = 0.5f;
        }};

        voidBlood = new Liquid("void-blood", Color.valueOf("2a0a3a")) {{
            localizedName = "Void Blood";
            description = "What the Abyss bleeds when wounded by transcendence-class constructs. " +
                "The civilization never wounded the Abyss. " +
                "We have. " +
                "It noticed.";
            heatCapacity = 1.5f;
            viscosity = 0.7f;
            temperature = 1.0f;
            flammability = 0.8f;
            explosiveness = 0.7f;
        }};
    }
}