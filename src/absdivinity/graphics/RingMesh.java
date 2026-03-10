package absdivinity.graphics;

import arc.graphics.*;
import arc.graphics.g3d.*;
import arc.math.*;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;

public class RingMesh extends PlanetMesh implements GenericMesh {
    public RingMesh(Planet planet, float radius, float width, int divisions, Color color){
        super(planet, GenMesh.ring(radius, width, divisions, color), Shaders.planet);
    }

    private static class GenMesh {
        static Mesh ring(float radius, float width, int divisions, Color color){
            Mesh mesh = new Mesh(true, divisions * 2, divisions * 6, new VertexAttribute[]{
                VertexAttribute.position3,
                VertexAttribute.color
            });

            float[] vertices = new float[divisions * 2 * 4];
            short[] indices = new short[divisions * 6];

            float inner = radius;
            float outer = radius + width;
            float packed = color.toFloatBits(); 

            for(int i = 0; i < divisions; i++){
                float angle = (float)i / divisions * Mathf.PI2;
                float x = Mathf.cos(angle);
                float z = Mathf.sin(angle);

                int vi = i * 8; 
                vertices[vi]     = x * inner;
                vertices[vi + 1] = 0f;
                vertices[vi + 2] = z * inner;
                vertices[vi + 3] = packed;

                vertices[vi + 4] = x * outer;
                vertices[vi + 5] = 0f;
                vertices[vi + 6] = z * outer;
                vertices[vi + 7] = packed;

                int ii = i * 6;
                short baseInner = (short)(i * 2);
                short baseOuter = (short)(i * 2 + 1);
                short nextInner = (short)((i + 1) % divisions * 2);
                short nextOuter = (short)((i + 1) % divisions * 2 + 1);

                indices[ii]     = baseInner;
                indices[ii + 1] = baseOuter;
                indices[ii + 2] = nextInner;
                indices[ii + 3] = nextInner;
                indices[ii + 4] = baseOuter;
                indices[ii + 5] = nextOuter;
            }

            mesh.setVertices(vertices);
            mesh.setIndices(indices);
            return mesh;
        }
    }
}