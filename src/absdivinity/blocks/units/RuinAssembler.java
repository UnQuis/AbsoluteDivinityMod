package absdivinity.blocks.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Items;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.world.blocks.units.UnitFactory;
import absdivinity.content.ADUnits;
import absdivinity.items.ADItems;
import absdivinity.items.ADLiquids;

public class RuinAssembler extends UnitFactory {

    public RuinAssembler(String name) {
        super(name);

        size            = 3;
        health          = 1200;
        armor           = 8f;
        hasLiquids      = true;
        liquidCapacity  = 60f;

        requirements(mindustry.type.Category.units, ItemStack.with(
            Items.titanium,      80,
            Items.thorium,       40,
            Items.plastanium,    30
        ));

        consumeLiquid(mindustry.content.Liquids.water, 0.05f);
        
        buildType = RuinAssemblerBuild::new;
    }

    public Color scanColor = Color.valueOf("4a8060");
    public Color ringColor = Color.valueOf("5a6060");

    public class RuinAssemblerBuild extends UnitFactoryBuild {

        float warmup  = 0f;
        float scanY   = 0f;
        float ringRot = 0f;

        @Override
        public void updateTile() {
            super.updateTile();
            boolean active = efficiency > 0 && currentPlan >= 0;
            warmup   = Mathf.lerpDelta(warmup, active ? 1f : 0f, 0.025f);
            ringRot += Time.delta * 0.9f * warmup;
            scanY    = (scanY + Time.delta * 0.8f * warmup) % (size * 16f * 2f);
        }

        @Override
        public void draw() {
            super.draw();
            if (warmup < 0.02f) return;

            float z  = Draw.z();
            float hw = size * 8f;

            Draw.z(Layer.blockOver);

            float sy = y - hw + scanY;
            if (sy <= y + hw) {
                float alpha = warmup * (0.5f + Mathf.absin(Time.time, 3f, 0.2f));
                Draw.color(scanColor, alpha);
                Lines.stroke(1.5f);
                Lines.line(x - hw, sy, x + hw, sy);
                Draw.color(scanColor, alpha * 0.12f);
                Fill.rect(x, sy - 4f, hw * 2f, 8f);
            }

            Draw.color(ringColor, warmup * 0.65f);
            Lines.stroke(2f);
            drawDashedRing(x, y, hw * 0.85f, ringRot,          8);
            Lines.stroke(1.5f);
            Draw.color(ringColor, warmup * 0.45f);
            drawDashedRing(x, y, hw * 0.6f,  -ringRot * 1.4f,  6);

            Draw.color(scanColor, warmup * 0.9f);
            Lines.stroke(1.5f);
            float m = hw * 0.9f, ml = 7f;
            for (int sx = -1; sx <= 1; sx += 2)
                for (int sy2 = -1; sy2 <= 1; sy2 += 2) {
                    Lines.line(x + sx*m,       y + sy2*m,       x + sx*(m - ml), y + sy2*m);
                    Lines.line(x + sx*m,       y + sy2*m,       x + sx*m,        y + sy2*(m - ml));
                }

            Draw.reset();
            Draw.z(z);
        }

        void drawDashedRing(float cx, float cy, float r, float rot, int segs) {
            for (int i = 0; i < segs; i += 2) {
                float a1 = (i      / (float) segs) * Mathf.PI2 + rot * Mathf.degreesToRadians;
                float a2 = ((i+1f) / (float) segs) * Mathf.PI2 + rot * Mathf.degreesToRadians;
                Lines.line(
                    cx + Mathf.cos(a1) * r, cy + Mathf.sin(a1) * r,
                    cx + Mathf.cos(a2) * r, cy + Mathf.sin(a2) * r
                );
            }
        }
    }
}