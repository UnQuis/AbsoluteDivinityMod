package absdivinity.graphics;

import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.VertexAttribute;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;
import java.nio.FloatBuffer;

public class CylinderRingMeshBuilder {
    public static Mesh build(float radius, float height, int segments, Color color, Color color2){
        Mesh mesh = new Mesh(true, segments * 18, 0, new VertexAttribute[]{
            VertexAttribute.position3,
            VertexAttribute.normal,
            VertexAttribute.color
        });

        FloatBuffer buf = mesh.getVerticesBuffer();
        buf.position(0);
        buf.limit(buf.capacity());

        float half = height / 2f;


        Vec3 p1 = new Vec3(), p2 = new Vec3(), p3 = new Vec3(), p4 = new Vec3();
        Vec3 p5 = new Vec3(), p6 = new Vec3(), p7 = new Vec3(), p8 = new Vec3();
        Vec3 normalSide = new Vec3(), temp1 = new Vec3(), temp2 = new Vec3();

        for(int i = 0; i < segments; i++){
            if(Mathf.chance(0.08)) continue;
            float col = (i % 4 == 0 ? color : color2).toFloatBits();

            float a1 = (float)i / segments * Mathf.PI2;
            float a2 = (float)(i + 1) / segments * Mathf.PI2;
            float radiusVar = radius + (float)Simplex.noise2d(1337, 4, 0.2, 0.5, i * 12f, 0f) * radius * 0.09f;

            p1.set(Mathf.cos(a1) * radiusVar,  half, Mathf.sin(a1) * radiusVar);
            p2.set(Mathf.cos(a2) * radiusVar,  half, Mathf.sin(a2) * radiusVar);
            p3.set(Mathf.cos(a2) * radiusVar, -half, Mathf.sin(a2) * radiusVar);
            p4.set(Mathf.cos(a1) * radiusVar, -half, Mathf.sin(a1) * radiusVar);

            float innerRad = radiusVar * 0.92f;
            p5.set(Mathf.cos(a1) * innerRad,  half, Mathf.sin(a1) * innerRad);
            p6.set(Mathf.cos(a2) * innerRad,  half, Mathf.sin(a2) * innerRad);
            p7.set(Mathf.cos(a2) * innerRad, -half, Mathf.sin(a2) * innerRad);
            p8.set(Mathf.cos(a1) * innerRad, -half, Mathf.sin(a1) * innerRad);

            normalSide.set(p1).sub(p2).crs(temp1.set(p1).sub(p4)).nor();

            vert(buf, p1, normalSide, col);
            vert(buf, p2, normalSide, col);
            vert(buf, p3, normalSide, col);
            vert(buf, p1, normalSide, col);
            vert(buf, p3, normalSide, col);
            vert(buf, p4, normalSide, col);

            vert(buf, p5, Vec3.Y, col);
            vert(buf, p6, Vec3.Y, col);
            vert(buf, p2, Vec3.Y, col);
            vert(buf, p5, Vec3.Y, col);
            vert(buf, p2, Vec3.Y, col);
            vert(buf, p1, Vec3.Y, col);

            temp2.set(Vec3.Y).scl(-1f);
            vert(buf, p8, temp2, col);
            vert(buf, p7, temp2, col);
            vert(buf, p3, temp2, col);
            vert(buf, p8, temp2, col);
            vert(buf, p3, temp2, col);
            vert(buf, p4, temp2, col);
        }

        buf.limit(buf.position());
        return mesh;
    }

    private static void vert(FloatBuffer buf, Vec3 pos, Vec3 normal, float color){
        buf.put(pos.x).put(pos.y).put(pos.z);
        buf.put(normal.x).put(normal.y).put(normal.z);
        buf.put(color);
    }
}