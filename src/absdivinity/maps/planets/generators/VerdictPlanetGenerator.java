package absdivinity.maps.planets.generators;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.world.Block;
import mindustry.world.meta.Attribute;
import mindustry.graphics.g3d.SunMesh;
import absdivinity.ADBlocks;
import absdivinity.items.ADItems;

public class VerdictPlanetGenerator extends PlanetGenerator{
    public float scl = 5f;
    public float waterThreshold = 0.15f;

    @Override
    public Color getColor(Vec3 position){
        float oasisNoise = (float)Simplex.noise3d(seed + 1, 4, 0.6, 0.4, position.x, position.y + 99, position.z);

        if (oasisNoise > 0.7f) return Color.valueOf("95c2ee").mul(1.2f);
        return Color.valueOf("333f41");
    }

    @Override
    public float getHeight(Vec3 position){
        float noise = Simplex.noise3d(seed, 8, 0.5, 1f/scl, position.x, position.y, position.z);
        return Math.max(noise, 0f);
    }

    public Block getBlock(Vec3 position){
        float h = getHeight(position);
        float oasisNoise = Simplex.noise3d(seed + 1, 4, 0.6, 0.4f, position.x, position.y, position.z);
        float corruption = (float)Simplex.noise3d(seed + 2, 3, 0.7, 0.7, position.x, position.y, position.z);

        if(corruption > 0.9f) return Blocks.tar;
        if(oasisNoise > 0.82f) return ADBlocks.diviniteOre;
        if(oasisNoise > 0.65f) return ADBlocks.oasisFloor;
        if(h < waterThreshold) return Blocks.arkyciteFloor;

        return ADBlocks.wastelandFloor;
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        return (float)Simplex.noise2d(seed, octaves, falloff, 1f / (float)scl, x, y) * (float)mag;
    }
}
