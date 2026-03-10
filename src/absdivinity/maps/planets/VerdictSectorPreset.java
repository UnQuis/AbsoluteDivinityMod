package absdivinity.maps.planets;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.game.*;
import mindustry.type.*;
import absdivinity.content.ADUnits;
import absdivinity.maps.planets.generators.VerdictPlanetGenerator;
import absdivinity.maps.planets.generators.VerdictPlanetGenerator.Biome;

public class VerdictSectorPreset {

    public static void apply(Sector sector, Rules rules) {
        VerdictPlanetGenerator gen = (VerdictPlanetGenerator) sector.planet.generator;

        Biome biome = gen.getSectorBiome(sector.id);

        float r1 = ((sector.id * 2654435761L) % 1000) / 1000f;        // primary random
        float r2 = ((sector.id * 40503L + 12345L) % 1000) / 1000f;    // secondary
        float diff = Mathf.clamp((r1 * 0.7f + r2 * 0.3f));            // 0.0 .. 1.0

        rules.pvp   = false;
        rules.fog   = false;
        rules.waves = true;
        rules.waveTimer = true;

        float waveBase = 60f * Mathf.lerp(30f, 90f, r2);
        rules.waveSpacing = (int)(waveBase * (1f - diff * 0.4f));

        rules.unitHealthMultiplier      = 1f + diff * 3.5f;
        rules.unitDamageMultiplier      = 1f + diff * 2.5f;
        rules.buildSpeedMultiplier       = 1f + diff * 0.25f;

        rules.spawns = buildWaves(biome, diff, sector.id);

        switch (biome) {
            case CORRUPTION:
                rules.unitDamageMultiplier  *= 1.4f;
                rules.buildSpeedMultiplier   *= 0.7f;
                break;
            case GLACIER:
                rules.unitDamageMultiplier    = 0.8f;
                rules.unitHealthMultiplier  *= 1.15f;
                break;
            case RUINS:
                rules.unitHealthMultiplier  *= 1.25f;
                rules.unitDamageMultiplier  *= 1.1f;
                break;
            case OASIS:
                rules.unitHealthMultiplier  *= 0.75f;
                rules.waveSpacing = (int)(rules.waveSpacing * 1.5f);
                break;
            case CRATER:
                // Кратер — много быстрых волн, мало здоровья
                rules.unitHealthMultiplier  *= 0.85f;
                rules.waveSpacing = (int)(rules.waveSpacing * 0.65f);
                break;
            case VOID_LAKE:
                rules.waves = false;
                break;
            default: break;
        }
    }

    static Seq<SpawnGroup> buildWaves(Biome biome, float diff, int sectorId) {
        Seq<SpawnGroup> spawns = new Seq<>();

        // Количество активных тиров зависит от случайной сложности
        // diff 0.0-0.2 → только T1
        // diff 0.2-0.4 → T1-T2
        // diff 0.4-0.6 → T1-T3
        // diff 0.6-0.8 → T1-T4
        // diff 0.8-0.9 → T1-T5
        // diff 0.9-1.0 → T1-T7 (боссы)
        int maxTier = diff < 0.2f ? 1
                    : diff < 0.4f ? 2
                    : diff < 0.6f ? 3
                    : diff < 0.8f ? 4
                    : diff < 0.9f ? 5
                    : diff < 0.95f ? 6 : 7;

        float scale = 0.3f + diff * 1.2f;   // базовый unitScaling
        float shld  = diff * 200f;           // базовые щиты

        switch (biome) {
            case CORRUPTION: case VOID_LAKE:
                addGroup(spawns, ADUnits.voidSpore,  scale * 1.2f, shld * 0f, 1, 2);
                addGroup(spawns, ADUnits.voidCreep,  scale * 0.8f, shld * 0f, 2, 3);
                break;
            case OASIS:
                addGroup(spawns, ADUnits.celestShard, scale * 1.1f, shld * 0f, 1, 2);
                addGroup(spawns, ADUnits.celestDrift, scale * 0.9f, shld * 0f, 2, 3);
                break;
            default:
                addGroup(spawns, ADUnits.scrapCrawler, scale * 1.2f, shld * 0f, 1, 2);
                addGroup(spawns, ADUnits.scrapHound,   scale * 0.8f, shld * 0f, 2, 3);
                break;
        }

        if (maxTier < 2) return spawns;

        switch (biome) {
            case CORRUPTION: case VOID_LAKE:
                addGroup(spawns, ADUnits.abyssGrub,   scale, shld * 0.5f, 3, 4);
                addGroup(spawns, ADUnits.abyssWalker, scale * 0.7f, shld * 0.6f, 5, 5);
                break;
            case OASIS:
                addGroup(spawns, ADUnits.neutroScout,   scale, shld * 0.4f, 3, 4);
                addGroup(spawns, ADUnits.neutroStriker, scale * 0.7f, shld * 0.6f, 5, 5);
                break;
            default:
                addGroup(spawns, ADUnits.ironStalker, scale, shld * 0.5f, 3, 4);
                addGroup(spawns, ADUnits.ironBrute,   scale * 0.6f, shld * 0.7f, 5, 5);
                break;
        }

        if (maxTier < 3) return spawns;

        switch (biome) {
            case CORRUPTION: case VOID_LAKE:
                addGroup(spawns, ADUnits.voidReaper,     scale * 0.8f, shld * 1.5f, 6, 5);
                addGroup(spawns, ADUnits.voidJuggernaut, scale * 0.4f, shld * 2.0f, 8, 6);
                break;
            case RUINS:
                addGroup(spawns, ADUnits.ruinGuard,  scale * 0.7f, shld * 1.5f, 6, 5);
                addGroup(spawns, ADUnits.ruinCannon, scale * 0.35f, shld * 2.0f, 8, 6);
                break;
            case GLACIER:
                addGroup(spawns, ADUnits.glacierSpike, scale * 0.8f, shld * 1.4f, 6, 5);
                addGroup(spawns, ADUnits.glacierHulk,  scale * 0.4f, shld * 1.8f, 8, 6);
                break;
            default:
                addGroup(spawns, ADUnits.ironStalker, scale * 0.8f, shld * 1.5f, 6, 5);
                addGroup(spawns, ADUnits.voidReaper,  scale * 0.5f, shld * 2.0f, 8, 6);
                break;
        }

        if (maxTier < 4) return spawns;

        switch (biome) {
            case CORRUPTION: case VOID_LAKE:
                addGroup(spawns, ADUnits.corruptedTitan,   scale * 0.35f, shld * 4f, 10, 7);
                addGroup(spawns, ADUnits.corruptedBehemoth,scale * 0.2f,  shld * 6f, 14, 8);
                break;
            default:
                addGroup(spawns, ADUnits.diviniteShield,  scale * 0.3f, shld * 4f, 10, 7);
                addGroup(spawns, ADUnits.diviniteRampage, scale * 0.2f, shld * 5f, 13, 8);
                break;
        }

        if (maxTier < 5) return spawns;

        switch (biome) {
            case CORRUPTION: case VOID_LAKE:
                addGroup(spawns, ADUnits.voidWraith,  scale * 0.5f, shld * 3f, 11, 6);
                addGroup(spawns, ADUnits.voidPhantom, scale * 0.3f, shld * 5f, 15, 7);
                break;
            default:
                addGroup(spawns, ADUnits.energyHawk,   scale * 0.5f, shld * 3f, 11, 6);
                addGroup(spawns, ADUnits.energyFalcon, scale * 0.25f, shld * 5f, 15, 7);
                break;
        }

        if (maxTier < 6) return spawns;

        addGroup(spawns, ADUnits.abyssSeraph,   scale * 0.2f, shld * 8f,  17, 8);
        addGroup(spawns, ADUnits.abyssArchangel,scale * 0.1f, shld * 12f, 21, 10);

        if (maxTier < 7) return spawns;

        addGroup(spawns, ADUnits.verdictSentinel,  scale * 0.05f, shld * 20f, 25, 15);
        addGroup(spawns, ADUnits.verdictAscendant, scale * 0.03f, shld * 25f, 30, 18);

        return spawns;
    }

    static void addGroup(Seq<SpawnGroup> list, UnitType type,
                         float scaling, float shields, int begin, int spacing) {
        SpawnGroup g = new SpawnGroup(type);
        g.unitScaling = Math.max(scaling, 0.05f);
        g.shields     = Math.max(shields, 0f);
        g.begin       = begin;
        g.spacing     = spacing;
        list.add(g);
    }
}