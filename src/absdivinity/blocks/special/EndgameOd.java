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
import mindustry.graphics.Layer;
import mindustry.type.Category;
import static mindustry.type.ItemStack.with;
import mindustry.world.blocks.defense.OverdriveProjector;

public class EndgameOd extends OverdriveProjector {
    public float maxShield = 5000f;
    public float maxRange  = 300f;
    public float maxBoost  = 70f;

    public EndgameOd(String name) {
        super(name);
        health = 2500;
        size = 3;
        hasPower = true;
        hasItems = true;
        configurable = true;
        saveConfig = true;

        requirements(Category.effect, with(
            Items.lead, 500,
            Items.silicon, 400,
            Items.phaseFabric, 150,
            Items.surgeAlloy, 100
        ));

        config(float[].class, (EndgameOdBuild build, float[] data) -> {
            build.sHealth = data[0];
            build.sRange  = data[1];
            build.sBoost  = data[2];
        });
    }

    public class EndgameOdBuild extends OverdriveBuild {
        public float sHealth = 1000f;
        public float sRange  = 120f;
        public float sBoost  = 2f;
        public float curShield = 0f;

        // --- Реальный буст с учётом энергии ---
        @Override
        public float realBoost() {
            // 1.0 = без ускорения; применяем sBoost пропорционально энергии
            return 1f + (sBoost - 1f) * power.graph.getSatisfaction();
        }

        // --- Основной апдейт ---
        @Override
        public void updateTile() {
            float sat = power.graph.getSatisfaction();

            // Щит восстанавливается при достаточной энергии
            if (sat > 0.5f) {
                float regenSpeed = (sHealth * 0.01f / 60f) * sat;
                curShield = Mathf.approachDelta(curShield, sHealth, regenSpeed);
            }

            // Передаём актуальный range в родителя ДО его updateTile
            range = sRange;

            // Плавное нарастание эффективности (как в AdaptOverdriveProjector)
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            // Когда накопился заряд — рассылаем буст всем зданиям в радиусе
            if (charge >= reload) {
                charge = 0f;
                Vars.indexer.eachBlock(
                    team,
                    Tmp.r1.setCentered(x, y, sRange),
                    other -> other.block.canOverdrive,
                    other -> other.applyBoost(realBoost(), reload + 1f)
                );
            }

            // Потребляем расходники (фаза и т.д.) по таймеру родителя
        }

        // --- UI конфигурации ---
        @Override
        public void buildConfiguration(Table table) {
            table.add("Projector Settings").padBottom(10).row();

            table.add("Integrity: " + (int)sHealth).row();
            table.slider(100f, maxShield, 100f, sHealth, n -> {
                sHealth = n;
                configure(new float[]{sHealth, sRange, sBoost});
            }).width(200f).padBottom(10).row();

            table.add("Radius: " + (int)sRange).row();
            table.slider(40f, maxRange, 10f, sRange, n -> {
                sRange = n;
                configure(new float[]{sHealth, sRange, sBoost});
            }).width(200f).padBottom(10).row();

            table.add("Boost: x" + sBoost).row();
            table.slider(1.1f, maxBoost, 0.1f, sBoost, n -> {
                sBoost = n;
                configure(new float[]{sHealth, sRange, sBoost});
            }).width(200f).row();
        }

        // --- Отрисовка щита ---
        @Override
        public void draw() {
            super.draw();
            if (curShield > 0 && power.graph.getSatisfaction() > 0.1f) {
                Draw.z(Layer.shields);
                float alpha = curShield / sHealth;
                Draw.color(Color.valueOf("feb380"), Color.white, alpha * 0.2f);
                Fill.poly(x, y, 6, sRange);
                Lines.stroke(2f * alpha);
                Draw.color(Color.valueOf("feb380"), alpha);
                Lines.poly(x, y, 6, sRange);
                Draw.reset();
            }
        }

        // --- Сохранение ---
        @Override
        public void write(arc.util.io.Writes write) {
            super.write(write);
            write.f(sHealth);
            write.f(sRange);
            write.f(sBoost);
            write.f(curShield);
        }

        @Override
        public void read(arc.util.io.Reads read, byte revision) {
            super.read(read, revision);
            sHealth  = read.f();
            sRange   = read.f();
            sBoost   = read.f();
            curShield = read.f();
        }
    }
}