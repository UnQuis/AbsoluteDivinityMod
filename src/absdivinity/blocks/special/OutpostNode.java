package absdivinity.blocks.special;

import static mindustry.type.ItemStack.with;
import absdivinity.items.ADItems;
import arc.util.*;
import arc.util.io.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.*;
import mindustry.*;
import mindustry.content.Items;
import mindustry.type.*;

public class OutpostNode extends Block {
    public float range = 160f;

    public OutpostNode(String name){
        super(name);
        update = true;
        solid = true;
        size = 2;
        configurable = true;

        config(Integer.class, (OutpostBuild build, Integer pos) -> {
            Building other = Vars.world.build(pos);
            if (other != null && other != build && other.within(build.x, build.y, range) && other.team == build.team) {
                build.toggleTarget(other);
            }
        });

        requirements(Category.effect, with(
            Items.metaglass, 340,
            Items.surgeAlloy, 180,
            Items.titanium, 250,
            Items.silicon, 400,
            ADItems.celestite, 400
        ));
    }

    public class OutpostBuild extends Building {
        public Seq<Building> targets = new Seq<>();
        public final int maxTargets = 15;

        public void toggleTarget(Building other) {
            if (targets.contains(other)) {
                targets.remove(other);
            } else if (targets.size < maxTargets) {
                targets.add(other);
            }
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if (this != other && other.within(x, y, range) && other.team == team){
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public void updateTile() {
            if (efficiency > 0) {
                Building core = team.core();
                if (core == null) return;

                for (int i = targets.size - 1; i >= 0; i--) {
                    Building target = targets.get(i);

                    if (target == null || !target.isValid() || target.team != team) {
                        targets.remove(i);
                        continue;
                    }

                    target.items.each((item, amount) -> {
                        if (target.block.consumesItem(item)) return;

                        int accepted = core.acceptStack(item, amount, target);
                        if (accepted > 0) {
                            core.handleStack(item, accepted, target);
                            target.items.remove(item, accepted);
                        }
                    });
                }
            }
        }

        @Override
        public void drawConfigure(){
            Draw.color(team.color);
            Lines.stroke(1.5f);
            Lines.circle(x, y, range);

            for (Building target : targets){
                if (target != null && target.isValid()){
                    Drawf.dashLine(team.color, x, y, target.x, target.y);
                    Drawf.square(target.x, target.y, target.block.size * Vars.tilesize / 2f + 1f, team.color);
                }
            }
            Draw.reset();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.i(targets.size);
            for(Building b : targets) write.i(b.pos());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            int size = read.i();
            for(int i = 0; i < size; i++){
                int pos = read.i();
                Time.run(10f, () -> {
                    Building b = Vars.world.build(pos);
                    if(b != null) targets.add(b);
                });
            }
        }
    }
}