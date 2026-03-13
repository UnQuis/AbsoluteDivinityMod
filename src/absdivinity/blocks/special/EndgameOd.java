package absdivinity.blocks.special;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import static mindustry.type.ItemStack.with;
import mindustry.world.blocks.defense.OverdriveProjector;

public class EndgameOd extends OverdriveProjector {

    public float maxShield      = 75000f;
    public float maxLocalShield = 10000f;
    public float maxRangePx     = 300f;
    public float maxBoost       = 70f;

    public float basePowerUse    = 1f;
    public float powerPerRangePx = 0.008f;

    public EndgameOd(String name) {
        super(name);
        health = 2500;
        size = 3;
        hasPower = true;
        hasItems = true;
        configurable = true;
        saveConfig = true;
        consumesPower = false;
        consumePower(0f);

        requirements(Category.effect, with(
            Items.lead,        500,
            Items.silicon,     400,
            Items.phaseFabric, 150,
            Items.surgeAlloy,  100
        ));

        config(float[].class, (EndgameOdBuild b, float[] d) -> {
            b.sHealth      = d[0];
            b.sLocalHealth = d[1];
            b.sRangePx     = d[2];
            b.sBoost       = d[3];
        });
    }

    public class EndgameOdBuild extends OverdriveBuild {

        public float sHealth      = 5000f;
        public float sLocalHealth = 10000f;
        public float sRangePx     = 120f;
        public float sBoost       = 2f;

        public float curShield      = 0f;
        public float curLocalShield = 0f;

        private float shieldHit      = 0f;
        private float localShieldHit = 0f;
        private float ringPulse      = 0f;
        private float rotAngle       = 0f;

        float dynamicPowerUse() { return basePowerUse + sRangePx * powerPerRangePx; }
        float realRangeTiles()  { return sRangePx / Vars.tilesize; }
        float localShieldR()    { return size * 8f * 1.8f; }

        @Override
        public float getPowerProduction() {
            return -dynamicPowerUse() * delta();
        }

        @Override
        public float realBoost() {
            return 1f + (sBoost - 1f) * power.graph.getSatisfaction();
        }

        @Override
        public void updateTile() {
            float sat = power.graph.getSatisfaction();

            if (sat > 0.5f) {
                curShield      = Mathf.approachDelta(curShield,      sHealth,      sHealth      * 0.005f / 60f * sat);
                curLocalShield = Mathf.approachDelta(curLocalShield, sLocalHealth, sLocalHealth * 0.008f / 60f * sat);
            }

            range = realRangeTiles();

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if (charge >= reload) {
                charge = 0f;
                ringPulse = 1f;
                Vars.indexer.eachBlock(
                    team,
                    Tmp.r1.setCentered(x, y, sRangePx),
                    other -> other.block.canOverdrive,
                    other -> other.applyBoost(realBoost(), reload + 1f)
                );
            }

            shieldHit      = Mathf.lerpDelta(shieldHit,      0f, 0.07f);
            localShieldHit = Mathf.lerpDelta(localShieldHit, 0f, 0.07f);
            ringPulse      = Mathf.lerpDelta(ringPulse,      0f, 0.04f);
            rotAngle      += Time.delta * 0.6f;
        }

        @Override
        public void damage(float amount) {
            if (curShield > 0) {
                float absorbed = Math.min(curShield, amount);
                curShield -= absorbed;
                amount    -= absorbed;
                shieldHit  = 1f;
                if (amount <= 0) return;
            }
            if (curLocalShield > 0) {
                float absorbed = Math.min(curLocalShield, amount);
                curLocalShield -= absorbed;
                amount         -= absorbed;
                localShieldHit  = 1f;
                if (amount <= 0) return;
            }
            super.damage(amount);
        }

        @Override
        public void draw() {
            super.draw();

            float sat = power.graph.getSatisfaction();
            if (sat < 0.05f) return;

            float z = Draw.z();

            float rangeAlpha = (sHealth > 0 ? curShield / sHealth : 0f) * sat;
            float localAlpha = (sLocalHealth > 0 ? curLocalShield / sLocalHealth : 0f) * sat;

            Draw.z(Layer.shields - 0.15f);
            Draw.color(0.5f, 0.7f, 1f, 0.03f * rangeAlpha);
            Fill.circle(x, y, sRangePx);

            Draw.z(Layer.shields);

            Draw.color(
                0.98f, 0.78f + shieldHit * 0.22f, 0.47f + shieldHit * 0.53f,
                (0.06f + shieldHit * 0.15f) * rangeAlpha
            );
            Fill.poly(x, y, 6, sRangePx);

            Lines.stroke(1.5f + shieldHit * 2.5f);
            Draw.color(
                0.98f, 0.78f + shieldHit * 0.22f, 0.47f + shieldHit * 0.53f,
                (0.45f + shieldHit * 0.4f) * rangeAlpha
            );
            Lines.poly(x, y, 6, sRangePx);

            if (ringPulse > 0.02f) {
                Draw.color(0.6f, 0.9f, 1f, ringPulse * 0.65f);
                Lines.stroke(2f * ringPulse);
                Lines.circle(x, y, sRangePx * (1f - ringPulse * 0.08f));
            }

            float lsr = localShieldR();

            Draw.color(
                0.4f + localShieldHit * 0.6f, 0.7f + localShieldHit * 0.2f, 1f,
                (0.07f + localShieldHit * 0.18f) * localAlpha
            );
            Fill.circle(x, y, lsr);

            Lines.stroke(2f + localShieldHit * 3f);
            Draw.color(
                0.4f + localShieldHit * 0.6f, 0.7f + localShieldHit * 0.2f, 1f,
                (0.55f + localShieldHit * 0.4f) * localAlpha
            );
            Lines.circle(x, y, lsr);

            Draw.z(Layer.blockOver);

            float innerR = size * 8f * 1.4f;
            Draw.color(0.98f, 0.78f, 0.47f, 0.4f * sat);
            Lines.stroke(1.2f);
            Lines.poly(x, y, 6, innerR, rotAngle);

            Draw.color(0.6f, 0.9f, 1f, 0.25f * sat);
            Lines.stroke(0.8f);
            Lines.poly(x, y, 6, innerR * 0.7f, -rotAngle * 1.3f);

            float corePulse = 1f + Mathf.absin(Time.time, 4f, 0.12f);
            Drawf.light(x, y, innerR * 3f * corePulse, Color.valueOf("feb380"), 0.35f * sat);
            Drawf.light(x, y, lsr * 1.5f, Color.valueOf("88aaff"), 0.2f * localAlpha);

            Draw.reset();
            Draw.z(z);
        }

        @Override
        public void buildConfiguration(Table table) {
            table.add("Projector Settings").padBottom(10).row();

            table.add("Field Integrity: " + (int)sHealth).row();
            table.slider(100f, maxShield, 100f, sHealth, n -> {
                sHealth = n;
                configure(new float[]{sHealth, sLocalHealth, sRangePx, sBoost});
            }).width(200f).padBottom(10).row();

            table.add("Core Shield: " + (int)sLocalHealth).row();
            table.slider(100f, maxLocalShield, 100f, sLocalHealth, n -> {
                sLocalHealth = n;
                configure(new float[]{sHealth, sLocalHealth, sRangePx, sBoost});
            }).width(200f).padBottom(10).row();

            table.add("Radius: " + (int)sRangePx + " px").row();
            table.slider(40f, maxRangePx, 10f, sRangePx, n -> {
                sRangePx = n;
                configure(new float[]{sHealth, sLocalHealth, sRangePx, sBoost});
            }).width(200f).padBottom(10).row();

            table.add("Boost: x" + sBoost).row();
            table.slider(1.1f, maxBoost, 0.1f, sBoost, n -> {
                sBoost = n;
                configure(new float[]{sHealth, sLocalHealth, sRangePx, sBoost});
            }).width(200f).row();
        }

        @Override
        public void write(arc.util.io.Writes write) {
            super.write(write);
            write.f(sHealth);
            write.f(sLocalHealth);
            write.f(sRangePx);
            write.f(sBoost);
            write.f(curShield);
            write.f(curLocalShield);
        }

        @Override
        public void read(arc.util.io.Reads read, byte revision) {
            super.read(read, revision);
            sHealth        = read.f();
            sLocalHealth   = read.f();
            sRangePx       = read.f();
            sBoost         = read.f();
            curShield      = read.f();
            curLocalShield = read.f();
        }
    }
}