package absdivinity.graphics;

import java.nio.FloatBuffer;

import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.VertexAttribute;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.noise.Simplex;

public class CylinderRingMeshBuilder {

    public static Mesh build(float radius, float height, int segments,
                             Color color, Color color2) {
        Mesh mesh = new Mesh(true, segments * 18, 0, new VertexAttribute[]{
                VertexAttribute.position3,
                VertexAttribute.normal,
                VertexAttribute.color
        });

        FloatBuffer buf = mesh.getVerticesBuffer();
        buf.clear();

        float half = height / 2f;
        // stride: xyz(3) + normal(3) + color(1) = 7 floats
        float[] f = new float[7];

        for (int i = 0; i < segments; i++) {
            if (Mathf.chance(0.07)) continue;

            float col = (i % 4 == 0 ? color : color2).toFloatBits();

            float a1 = (float) i      / segments * Mathf.PI2;
            float a2 = (float)(i + 1) / segments * Mathf.PI2;

            float rVar = radius + (float) Simplex.noise2d(1337, 4, 0.2, 0.5,
                    i * 12f, 0f) * radius * 0.07f;

            Vec3 p1 = new Vec3(Mathf.cos(a1) * rVar,  half, Mathf.sin(a1) * rVar);
            Vec3 p2 = new Vec3(Mathf.cos(a2) * rVar,  half, Mathf.sin(a2) * rVar);
            Vec3 p3 = new Vec3(Mathf.cos(a2) * rVar, -half, Mathf.sin(a2) * rVar);
            Vec3 p4 = new Vec3(Mathf.cos(a1) * rVar, -half, Mathf.sin(a1) * rVar);

            float inner = rVar * 0.968f;
            Vec3 p5 = new Vec3(Mathf.cos(a1) * inner,  half, Mathf.sin(a1) * inner);
            Vec3 p6 = new Vec3(Mathf.cos(a2) * inner,  half, Mathf.sin(a2) * inner);
            Vec3 p7 = new Vec3(Mathf.cos(a2) * inner, -half, Mathf.sin(a2) * inner);
            Vec3 p8 = new Vec3(Mathf.cos(a1) * inner, -half, Mathf.sin(a1) * inner);

            Vec3 nOut  = new Vec3(p1.x, 0, p1.z).nor();
            Vec3 nUp   = new Vec3(0, -1,  0);
            Vec3 nDown = new Vec3(0,  1,  0);

            // Внешняя стенка
            vert(buf, f, p1, nOut,  col);
            vert(buf, f, p2, nOut,  col);
            vert(buf, f, p3, nOut,  col);
            vert(buf, f, p1, nOut,  col);
            vert(buf, f, p3, nOut,  col);
            vert(buf, f, p4, nOut,  col);

            // Верхняя грань
            vert(buf, f, p1, nUp,   col);
            vert(buf, f, p2, nUp,   col);
            vert(buf, f, p6, nUp,   col);
            vert(buf, f, p1, nUp,   col);
            vert(buf, f, p6, nUp,   col);
            vert(buf, f, p5, nUp,   col);

            // Нижняя грань
            vert(buf, f, p8,  nDown, col);
            vert(buf, f, p7,  nDown, col);
            vert(buf, f, p3,  nDown, col);
            vert(buf, f, p8,  nDown, col);
            vert(buf, f, p3,  nDown, col);
            vert(buf, f, p4,  nDown, col);
        }

        buf.limit(buf.position());
        return mesh;
    }

    private static void vert(FloatBuffer buf, float[] f,
                              Vec3 p, Vec3 n, float color) {
        f[0] = p.x; f[1] = p.y; f[2] = p.z;
        f[3] = n.x; f[4] = n.y; f[5] = n.z;
        f[6] = color;
        buf.put(f);
    }
}