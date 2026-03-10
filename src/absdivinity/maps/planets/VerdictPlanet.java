package absdivinity.maps.planets;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Planets;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import absdivinity.graphics.DysonRingMesh;
import absdivinity.maps.planets.generators.VerdictPlanetGenerator;

public class VerdictPlanet {
    public static Planet verdict;
    public static DysonRingMesh mainRing, debrisRing, innerRing;

    public static void load() {
        verdict = new Planet("verdict", Planets.sun, 1.2f, 3) {{
            localizedName   = "Вердикт";
            generator       = new VerdictPlanetGenerator();

            bloom           = true;
            hasAtmosphere   = true;
            atmosphereColor = Color.valueOf("1e1528");
            atmosphereRadIn  = 0.025f;
            atmosphereRadOut = 0.28f;

            accessible     = true;
            alwaysUnlocked = true;
            visible        = true;

            iconColor = Color.valueOf("bf92f9").mul(0.75f);
        }};

        innerRing  = new DysonRingMesh(verdict, 1.38f, 0.08f, 512,
                Color.valueOf("bf92f9").mul(0.4f),
                Color.valueOf("312e44").mul(0.8f));

        mainRing   = new DysonRingMesh(verdict, 1.52f, 0.22f, 1337,
                Color.valueOf("333a41"),
                Color.valueOf("4a5055"));

        debrisRing = new DysonRingMesh(verdict, 1.88f, 0.055f, 2048,
                Color.valueOf("c8a830").mul(0.65f),
                Color.valueOf("ffd700").mul(0.35f));


        verdict.meshLoader = () -> {
            Seq<GenericMesh> meshes = new Seq<>();
            meshes.add(new HexMesh(verdict, 6));
            meshes.add(innerRing);
            meshes.add(mainRing);
            meshes.add(debrisRing);
            return new MultiMesh(meshes.toArray(GenericMesh.class));
        };
    }
}