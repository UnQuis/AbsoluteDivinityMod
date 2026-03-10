package absdivinity.maps.planets.generators;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.Schematics;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import absdivinity.ADBlocks;

public class VerdictPlanetGenerator extends PlanetGenerator {

    public float scl            = 5f;
    public float waterThreshold = 0.18f;

    public enum Biome {
        WASTELAND,   // Металлическая пустошь
        OASIS,       // Оазис целестита
        CORRUPTION,  // Зона коррупции
        CRATER,      // Кратер
        RUINS,       // Руины цивилизации
        GLACIER,     // Ледник (ванильный лёд)
        VOID_LAKE    // Озеро из аркицита — посадка запрещена
    }

    static final Color C_WASTE  = Color.valueOf("2e3538");
    static final Color C_OASIS  = Color.valueOf("7aa8cc");
    static final Color C_CORRUPT= Color.valueOf("1a1520");
    static final Color C_CRATER = Color.valueOf("1c2226");
    static final Color C_RUINS  = Color.valueOf("3a3a42");
    static final Color C_GLACIER= Color.valueOf("9bb8cc");
    static final Color C_VOID   = Color.valueOf("0d0d12");

    @Override
    public Color getColor(Vec3 p) {
        float h = getHeight(p);
        switch (getBiome3D(p)) {
            case OASIS:    return C_OASIS.cpy().mul(0.9f + n3(seed+1,4,0.6,0.4,p)*0.3f);
            case CORRUPTION:return C_CORRUPT.cpy().mul(1f + n3(seed+2,3,0.7,0.7,p)*0.3f);
            case CRATER:   return C_CRATER.cpy().mul(0.8f + h*0.4f);
            case RUINS:    return C_RUINS.cpy().mul(0.9f + h*0.2f);
            case GLACIER:  return C_GLACIER.cpy().mul(0.85f + h*0.25f);
            case VOID_LAKE:return C_VOID;
            default:       return C_WASTE.cpy().mul(0.85f + h*0.4f);
        }
    }

    @Override
    public float getHeight(Vec3 p) {
        float base   = Simplex.noise3d(seed, 8, 0.5, 1f/scl, p.x, p.y, p.z);
        float ridges = Ridged.noise3d(seed+10, p.x, p.y, p.z, 4, 1.8f) * 0.35f;
        if (getBiome3D(p) == Biome.CRATER) return Math.max(base*0.4f, 0f);
        return Math.max(base + ridges*0.3f, 0f);
    }

    public Biome getBiome3D(Vec3 p) {
        float temp   = n3(seed+20, 3, 0.5, 0.6, p);
        float humid  = n3(seed+21, 3, 0.5, 0.6, p);
        float ruins  = n3(seed+22, 2, 0.4, 1.2, p);
        float crater = n3(seed+23, 2, 0.4, 0.9, p);
        float glacier= n3(seed+24, 2, 0.5, 0.7, p);
        float h      = Simplex.noise3d(seed, 8, 0.5, 1f/scl, p.x, p.y, p.z);

        if (crater  > 0.78f) return Biome.CRATER;
        if (ruins   > 0.80f) return Biome.RUINS;
        if (glacier > 0.75f && temp < 0.3f) return Biome.GLACIER;
        if (temp < 0.32f && humid > 0.55f)  return Biome.OASIS;
        if (humid   > 0.72f) return Biome.CORRUPTION;
        if (h < waterThreshold) return Biome.VOID_LAKE;
        return Biome.WASTELAND;
    }

    public Biome getSectorBiome(int id) {
        float px = (id % 37) / 37f * 10f;
        float py = (id / 37) / 37f * 10f;

        float temp   = (float)Simplex.noise2d(seed+20, 3, 0.5, 0.6, px, py);
        float humid  = (float)Simplex.noise2d(seed+21, 3, 0.5, 0.6, px, py);
        float ruins  = (float)Simplex.noise2d(seed+22, 2, 0.4, 1.2, px, py);
        float crater = (float)Simplex.noise2d(seed+23, 2, 0.4, 0.9, px, py);
        float glacier= (float)Simplex.noise2d(seed+24, 2, 0.5, 0.7, px, py);

        if (crater  > 0.72f) return Biome.CRATER;
        if (ruins   > 0.76f) return Biome.RUINS;
        if (glacier > 0.70f && temp < 0.33f) return Biome.GLACIER;
        if (temp < 0.35f && humid > 0.50f)   return Biome.OASIS;
        if (humid   > 0.68f) return Biome.CORRUPTION;
        if (((id * 1234567) % 100) < 12) return Biome.VOID_LAKE;
        return Biome.WASTELAND;
    }

    public float getDifficulty(Sector sector) {
        Vec3 sv = sector.tile.v;
        float dist = sv.dst(0f, 1f, 0f);
        return Mathf.clamp(dist / 1.8f);
    }

    @Override
    public int getSectorSize(Sector sector) {
        float d = getDifficulty(sector);
        return (int)Mathf.lerp(200f, 350f, d);
    }

    @Override
    public void genTile(Vec3 p, TileGen tile) {
        tile.floor = getFloor(p);
        tile.block = tile.floor.asFloor().wall;
        float rock = Ridged.noise3d(seed+77, p.x, p.y, p.z, 3, 9f);
        if (rock > -0.38f) tile.block = Blocks.air;
    }

    public Floor getFloor(Vec3 p) {
        float h      = getHeight(p);
        float oasis  = n3(seed+1, 4, 0.6, 0.4, p);
        float corrupt= n3(seed+2, 3, 0.7, 0.7, p);
        float detail = n3(seed+5, 4, 0.5, 2.0, p);

        switch (getBiome3D(p)) {
            case VOID_LAKE:  return Blocks.arkyciteFloor.asFloor();
            case GLACIER:
                if (detail > 0.6f) return Blocks.snow.asFloor();
                return Blocks.ice.asFloor();
            case CORRUPTION:
                if (corrupt > 0.90f) return Blocks.tar.asFloor();
                return (Floor)ADBlocks.wastelandFloor;
            case OASIS:
                if (oasis > 0.82f) return (Floor)ADBlocks.diviniteOre;
                return (Floor)ADBlocks.oasisFloor;
            case CRATER:
                return Blocks.arkyciteFloor.asFloor();
            case RUINS:
                if (detail > 0.65f) return Blocks.metalFloor5.asFloor();
                if (detail > 0.40f) return Blocks.metalFloor.asFloor();
                return (Floor)ADBlocks.wastelandFloor;
            default:
                if (h < waterThreshold) return Blocks.arkyciteFloor.asFloor();
                return (Floor)ADBlocks.wastelandFloor;
        }
    }

    @Override
    protected void generate() {
        Biome  biome = getSectorBiome(sector.id);
        float  diff  = getDifficulty(sector);

        distort(8, 14);
        median(2);

        rand.setSeed(seed + sector.id);
        int shift = rand.random(15, 85);

        switch (biome) {
            case WASTELAND:
                applyFaults(shift);
                pass((x, y) -> {
                    if (floor == ADBlocks.oasisFloor && noise(x,y,5,0.6f,12f,1f) > 1.35f)
                        ore = ADBlocks.diviniteOre;
                });
                if (diff > 0.5f) pass((x, y) -> {
                    if (floor == ADBlocks.wastelandFloor && noise(x,y,4,0.5f,15f,1f) > 1.4f)
                        ore = ADBlocks.diviniteOre;
                });
                break;

            case OASIS:
                pass((x, y) -> {
                    if (floor == ADBlocks.wastelandFloor && noise(x,y,4,0.5f,20f,1f) > 1.1f)
                        floor = ADBlocks.oasisFloor.asFloor();
                    if (floor == ADBlocks.oasisFloor && noise(x,y,4,0.6f,10f,1f) > 1.1f)
                        ore = ADBlocks.diviniteOre;
                });
                break;

            case CORRUPTION:
                pass((x, y) -> {
                    if (!floor.asFloor().isLiquid && noise(x,y,5,0.7f,18f,1f) > 1.3f)
                        floor = Blocks.tar.asFloor();
                });
                break;

            case CRATER:
                pass((x, y) -> {
                    float dist = Mathf.dst(x - width/2f, y - height/2f);
                    float rim  = width * 0.38f;
                    if (dist > rim && dist < rim + 8f)
                        block = ADBlocks.wastelandFloor.asFloor().wall;
                });
                pass((x, y) -> {
                    if (floor == Blocks.arkyciteFloor && noise(x,y,4,0.5f,14f,1f) > 1.2f)
                        ore = ADBlocks.diviniteOre;
                });
                break;

            case RUINS:
                applyFaults(shift);
                pass((x, y) -> {
                    if (floor == ADBlocks.wastelandFloor) {
                        if ((x % 18 < 3 || y % 18 < 3) && noise(x,y,3,0.5f,10f,1f) > 0.8f)
                            floor = Blocks.metalFloor.asFloor();
                    }
                });
                pass((x, y) -> {
                    if (floor == Blocks.metalFloor && rand.chance(0.04f))
                        block = Blocks.scrapWall;
                    if (floor == Blocks.metalFloor5 && rand.chance(0.1f))
                        ore = ADBlocks.diviniteOre;
                });
                break;

            case GLACIER:
                pass((x, y) -> {
                    if ((floor == Blocks.ice || floor == Blocks.snow)
                            && noise(x,y,4,0.6f,16f,1f) > 1.35f) {
                        floor = Blocks.iceSnow.asFloor();
                    }
                });
                pass((x, y) -> {
                    if (floor == Blocks.ice && rand.chance(0.015f))
                        ore = ADBlocks.diviniteOre;
                });
                break;

            case VOID_LAKE:
                pass((x, y) -> {
                    float n = noise(x, y, 6, 0.5f, 25f, 1f);
                    if (!floor.asFloor().isLiquid && n < 0.85f)
                        floor = Blocks.arkyciteFloor.asFloor();
                });
                break;
        }

        distort(4, 4);

        Vec2 trns = Tmp.v1.trns(rand.random(360f), width / 2.8f);
        int coreX = (int)(-trns.x + width/2f);
        int coreY = (int)(-trns.y + height/2f);

        for (int cx = coreX-5; cx <= coreX+5; cx++)
            for (int cy = coreY-5; cy <= coreY+5; cy++) {
                if (cx < 0 || cy < 0 || cx >= width || cy >= height) continue;
                tiles.get(cx, cy).setBlock(Blocks.air);
                tiles.get(cx, cy).setFloor(ADBlocks.wastelandFloor.asFloor());
                tiles.get(cx, cy).setOverlay(Blocks.air);
            }

        Schematics.placeLaunchLoadout(coreX, coreY);
    }

    @Override
    public boolean allowLanding(Sector sector) {
        return getSectorBiome(sector.id) != Biome.VOID_LAKE;
    }

    void applyFaults(int shift) {
        each((x, y) -> {
            Tile t = tiles.get(x, y);
            if (t.solid() || t.floor().asFloor().isLiquid) return;
            if (isOnFault(x, y, shift, 0) || isOnFault(x, y, shift, 1)) {
                float ln = noise(x, y, 4, 0.6f, 18f, 2.5f);
                if (ln > 0.9f) { t.setBlock(Blocks.air); t.setFloor(Blocks.arkyciteFloor.asFloor()); }
            }
        });
    }

    public boolean isOnFault(int x, int y, int s, int o) {
        int sp = 110;
        int n1 = (sp + s + o*2) % sp, n2 = (sp + s - o*2) % sp;
        return x%sp==n1 || x%sp==n2 || y%sp==n1 || y%sp==n2;
    }

    float n3(int s, double oct, double fall, double scl, Vec3 p) {
        return Simplex.noise3d(s, oct, fall, scl, p.x, p.y, p.z);
    }

    @Override
    protected float noise(float x, float y, double oct, double fall, double scl, double mag) {
        return (float)Simplex.noise2d(seed, oct, fall, 1f/(float)scl, x, y) * (float)mag;
    }
}