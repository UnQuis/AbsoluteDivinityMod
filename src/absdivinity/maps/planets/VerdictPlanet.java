package absdivinity.maps.planets;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import mindustry.content.*;
import mindustry.content.Planets;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import absdivinity.maps.planets.generators.VerdictPlanetGenerator;
import absdivinity.graphics.DysonRingMesh;
import absdivinity.graphics.RingMesh;

public class VerdictPlanet {
    public static Planet verdict;
    public static DysonRingMesh mainRing, debrisRing;

    public static void load(){
        verdict = new Planet("verdict", Planets.sun, 1.2f, 3){{
            localizedName = "Вердикт";
            generator = new VerdictPlanetGenerator();
            mainRing = new DysonRingMesh(verdict, 1.5f, 0.25f, 160, Color.valueOf("333a41"), Color.valueOf("5f5f5f"));
            debrisRing = new DysonRingMesh(verdict, 1.85f, 0.06f, 100, Color.valueOf("ffd700").mul(0.7f), Color.valueOf("ffd700").mul(0.4f));
            meshLoader = () -> {
                Seq<GenericMesh> meshes = new Seq<>();
                meshes.add(new HexMesh(this, 6));
                meshes.add(mainRing);
                meshes.add(debrisRing);

                return new MultiMesh(meshes.toArray(GenericMesh.class));
            };

            bloom = true;
            hasAtmosphere = true;
            atmosphereColor = Color.valueOf("312e44");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;

            accessible = true;
            alwaysUnlocked = true;
            visible = true;

            iconColor =  Color.valueOf("bf92f9").mul(0.7f);
        }};
    }
}
