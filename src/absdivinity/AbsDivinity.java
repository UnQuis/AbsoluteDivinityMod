package absdivinity;

import absdivinity.blocks.EnergyCore;
import absdivinity.blocks.production.*;
import absdivinity.blocks.special.*;
import absdivinity.blocks.turrets.*;
import absdivinity.content.ADTechTree;
import absdivinity.items.ADItems;
import absdivinity.items.ADLiquids;
import absdivinity.maps.planets.VerdictPlanet;
import absdivinity.maps.planets.generators.VerdictPlanetGenerator;
import mindustry.mod.*;
import arc.util.*;
import mindustry.world.*;
import mindustry.type.*;
import mindustry.content.*;

public class AbsDivinity extends Mod {
    public static UnitType energyCoreDrone;
    public static OmniTurret omniTurret;
    public static RayDrill rayDrill;
    public static RangeDrill rangeDrill;
    public static EnergyCore energyCore;
    public static EndgameOd endgameOd;
    public static BaHion baHion;
    public static OutpostNode outpostNode;
    public static VerdictPlanetGenerator generator;

    public AbsDivinity() {
        Log.info("Загрузка мода Absolute Divinity..."); 
    }
    @Override
    public void loadContent() {
        ADItems.load();
        ADLiquids.load();
        ADBlocks.load();
        VerdictPlanet.load();
        energyCoreDrone = new EnergyCoreDrone("energy-core-drone");
        omniTurret = new OmniTurret ("omni-turret");
        rayDrill = new RayDrill("ray-drill");
        rangeDrill = new RangeDrill("range-drill");
        energyCore = new EnergyCore("energy-core");
        endgameOd = new EndgameOd("endgame-od");
        baHion = new BaHion("ba-hion");
        outpostNode = new OutpostNode("outpost-node");
        generator = new VerdictPlanetGenerator();

        ADTechTree.load();
    }
}