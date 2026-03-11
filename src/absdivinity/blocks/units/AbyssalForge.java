package absdivinity.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.world.blocks.units.UnitFactory;
import absdivinity.content.ADUnits;
import absdivinity.items.ADItems;
import absdivinity.items.ADLiquids;

public class AbyssalForge extends UnitFactory {

    public AbyssalForge(String name) {
        super(name);

        size           = 5;
        health         = 4000;
        armor          = 20f;
        hasLiquids     = true;
        liquidCapacity = 200f;

        requirements(mindustry.type.Category.units, ItemStack.with(
            Items.titanium,   200,
            Items.thorium,    150,
            Items.surgeAlloy, 80,
            Items.phaseFabric, 60
        ));
        consumeLiquid(Liquids.cryofluid, 0.12f);
        
        buildType = AbyssalForgeBuild::new;
    }


    public Color coreColor  = Color.valueOf("bf92f9");
    public Color ringColorA = Color.valueOf("9060c8");
    public Color ringColorB = Color.valueOf("312e44");

    public class AbyssalForgeBuild extends UnitFactoryBuild {

        float   warmup    = 0f;
        float   pulse     = 0f;
        float[] ringRot   = { 0f, 0f, 0f };
        float[] ringSpd   = { 1.1f, -0.7f, 0.45f };
        float[] ringRadF  = { 0.75f, 0.90f, 1.05f };

        @Override
        public void updateTile() {
            super.updateTile();
            boolean active = efficiency > 0 && currentPlan >= 0;
            warmup = Mathf.lerpDelta(warmup, active ? 1f : 0f, 0.02f);
            pulse += Time.delta;
            for (int i = 0; i < ringRot.length; i++)
                ringRot[i] += Time.delta * ringSpd[i] * warmup;
        }

        @Override
        public void draw() {
            super.draw();
            if (warmup < 0.02f) return;

            float z  = Draw.z();
            float hw = size * 8f;

            Draw.z(Layer.blockOver);

            float coreR = hw * 0.35f * warmup * (1f + Mathf.absin(pulse, 5f, 0.08f));
            Draw.color(coreColor, warmup * (0.3f + Mathf.absin(pulse, 4f, 0.15f)));
            Fill.circle(x, y, coreR);

            Color[] cols = { ringColorA, coreColor, ringColorB };
            for (int i = 0; i < 3; i++) {
                float r     = hw * ringRadF[i] * warmup;
                float alpha = warmup * (0.6f + Mathf.absin(pulse + i * 30f, 6f, 0.25f));
                Draw.color(cols[i], alpha);
                Lines.stroke(2.5f - i * 0.4f);
                drawSegRing(x, y, r, ringRot[i], 12 - i * 2);
            }

            Draw.color(coreColor, warmup * 0.35f);
            Lines.stroke(1f);
            for (int i = 0; i < 6; i++) {
                float ang = ringRot[0] + i * 60f;
                float r   = hw * ringRadF[0] * warmup;
                Lines.line(x, y,
                    x + Mathf.cosDeg(ang) * r,
                    y + Mathf.sinDeg(ang) * r);
            }

            if (currentPlan >= 0 && efficiency > 0) {
                float prog = craftFraction();
                Draw.color(coreColor, prog * warmup * 0.7f);
                Lines.stroke(2f * prog);
                float sr = hw * 0.55f;
                for (int i = 0; i < 4; i++) {
                    float ang  = pulse * 1.5f + i * 90f;
                    float px   = x + Mathf.cosDeg(ang) * sr;
                    float py   = y + Mathf.sinDeg(ang) * sr;
                    float midX = (x + px) / 2 + Mathf.sinDeg(pulse * 2 + i * 80) * 20f;
                    float midY = (y + py) / 2 + Mathf.cosDeg(pulse * 2 + i * 80) * 20f;
                    Lines.curve(x, y, midX, midY, midX, midY, px, py, 8);
                }
            }

            Draw.color(coreColor, warmup * 0.9f);
            Lines.stroke(2f);
            float m = hw * 0.92f, ml = 10f;
            for (int sx = -1; sx <= 1; sx += 2)
                for (int sy = -1; sy <= 1; sy += 2) {
                    Lines.line(x + sx*m,       y + sy*m,       x + sx*(m - ml), y + sy*m);
                    Lines.line(x + sx*m,       y + sy*m,       x + sx*m,        y + sy*(m - ml));
                }

            float fr = craftFraction();
            if (fr > 0.97f) {
                float p = Interp.pow3Out.apply((fr - 0.97f) / 0.03f);
                Draw.color(Color.white, coreColor, 1f - p);
                Draw.alpha(p * 0.5f);
                Fill.circle(x, y, hw * 0.5f * p);
            }

            Draw.reset();
            Draw.z(z);
        }

        void drawSegRing(float cx, float cy, float r, float rot, int segs) {
            for (int i = 0; i < segs; i += 2) {
                float a1 = (i      / (float) segs) * Mathf.PI2 + rot * Mathf.degreesToRadians;
                float a2 = ((i+1f) / (float) segs) * Mathf.PI2 + rot * Mathf.degreesToRadians;
                Lines.line(
                    cx + Mathf.cos(a1) * r, cy + Mathf.sin(a1) * r,
                    cx + Mathf.cos(a2) * r, cy + Mathf.sin(a2) * r
                );
            }
        }

        float craftFraction() {
            if (currentPlan < 0 || plans == null || currentPlan >= plans.size) return 0f;
            float total = plans.get(currentPlan).time;
            return total <= 0 ? 0f : Mathf.clamp(progress / total);
        }
    }
}