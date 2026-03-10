package absdivinity.omni;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;
import mindustry.world.meta.Stat;

public class UniversalDrillComp extends Drill {
    public int scanRange = 3;

    public UniversalDrillComp(String name) {
        super(name);
        hasPower = true;
        update = true;
        hasItems = true;
        consumePower(1.5f);
    }

    @Override
    public boolean canPlaceOn(Tile tile, mindustry.game.Team team, int rotation) {
        int tx = tile.x, ty = tile.y;
        for (int dx = -scanRange; dx <= scanRange; dx++) {
            for (int dy = -scanRange; dy <= scanRange; dy++) {
                Tile other = Vars.world.tile(tx + dx, ty + dy);
                if (other != null && canMine(other)) return true;
            }
        }
        return false;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.basePowerGeneration, "Динамическое потребление зависит от твердости найденных руд");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Draw.color(Pal.accent);
        Lines.stroke(1f);
        Lines.square(x * Vars.tilesize + offset, y * Vars.tilesize + offset, (scanRange + 0.5f) * Vars.tilesize);
        Draw.reset();
        super.drawPlace(x, y, rotation, valid);
    }

    public class UniversalDrillBuild extends DrillBuild {
        public Seq<Item> myItems = new Seq<>();
        public ObjectIntMap<Item> myOreCount = new ObjectIntMap<>();

        public float progress = 0f;
        public float powerRequired = 1.5f;

        private void rescan() {
            myItems.clear();
            myOreCount.clear();

            int tx = tile.x, ty = tile.y;
            for (int dx = -scanRange; dx <= scanRange; dx++) {
                for (int dy = -scanRange; dy <= scanRange; dy++) {
                    Tile other = Vars.world.tile(tx + dx, ty + dy);
                    if (other != null && canMine(other)) {
                        Item drop = getDrop(other);
                        if (drop == null) continue;
                        myOreCount.increment(drop, 0, 1);
                        if (!myItems.contains(drop)) myItems.add(drop);
                    }
                }
            }

            powerRequired = 1.5f;
            for (Item item : myItems) {
                powerRequired += (item.hardness * 0.2f) * myOreCount.get(item, 0);
            }
        }


        @Override
        public boolean shouldConsume() {
            if (myItems.isEmpty()) return false;
            for (Item item : myItems) {
                if (items.get(item) < itemCapacity) return true;
            }
            return false;
        }


        @Override
        public void placed() {
            super.placed();
            rescan();
        }

        @Override
        public void onProximityUpdate() {}


        @Override
        public void updateTile() {
            if (timer(timerDump, dumpTime)) dump();

            if (efficiency <= 0 || !shouldConsume()) return;

            progress += edelta();

            if (progress >= drillTime) {
                progress %= drillTime;

                for (Item item : myItems) {
                    int count = myOreCount.get(item, 0);
                    if (count <= 0) continue;
                    if (items.get(item) >= itemCapacity) continue;

                    int toAdd = Math.min(count, itemCapacity - items.get(item));
                    items.add(item, toAdd);
                }
            }
        }

        @Override
        public float getPowerProduction() {
            return -powerRequired * efficiency;
        }

        @Override
        public void write(arc.util.io.Writes write) {
            super.write(write);
            write.f(progress);
            write.f(powerRequired);
        }

        @Override
        public void read(arc.util.io.Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            powerRequired = read.f();
            rescan();
        }
    }
}