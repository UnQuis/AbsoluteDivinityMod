package absdivinity;

import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;

public class ADGifProcessor {

    public static class GifAnimation {
        public final TextureRegion[] frames;
        public final int[]           delaysMs;
        public final int             totalMs;

        private float timer = 0f;
        private int   current = 0;

        GifAnimation(TextureRegion[] frames, int[] delaysMs) {
            this.frames   = frames;
            this.delaysMs = delaysMs;
            int total = 0;
            for (int d : delaysMs) total += d;
            this.totalMs = Math.max(total, 1);
        }

        public TextureRegion frame() {
            timer += arc.util.Time.delta * (1000f / 60f);
            while (timer >= delaysMs[current]) {
                timer -= delaysMs[current];
                current = (current + 1) % frames.length;
            }
            return frames[current];
        }

        public TextureRegion peek() {
            return frames[current];
        }

        public int index() { return current; }

        public int size() { return frames.length; }

        public boolean isEmpty() { return frames.length == 0; }

        public void dispose() {
            for (TextureRegion r : frames)
                if (r.texture != null) r.texture.dispose();
        }
    }

    /**
     * Load a GIF from the mod's asset folder.
     * @param internalPath path relative to mod root, e.g. "sprites/my-anim.gif"
     * @return GifAnimation (empty if loading fails)
     */
    public static GifAnimation load(String internalPath) {
        try {
            mindustry.mod.Mods.LoadedMod modRoot = Vars.mods.getMod("AbsoluteDivinityMod");
            if (modRoot == null) {
                Log.err("[ADGifProcessor] Mod root not found");
                return empty();
            }

            arc.files.Fi file = modRoot.root.child(internalPath);
            if (!file.exists()) {
                Log.err("[ADGifProcessor] GIF not found: " + internalPath);
                return empty();
            }

            Seq<ADGifDecoder.GifFrame> decoded = ADGifDecoder.decode(file.read());
            if (decoded.isEmpty()) {
                Log.err("[ADGifProcessor] No frames decoded from: " + internalPath);
                return empty();
            }

            TextureRegion[] regions = new TextureRegion[decoded.size];
            int[]           delays  = new int          [decoded.size];

            for (int i = 0; i < decoded.size; i++) {
                ADGifDecoder.GifFrame f = decoded.get(i);
                Texture tex = new Texture(f.pixmap);
                tex.setFilter(Texture.TextureFilter.nearest, Texture.TextureFilter.nearest);
                regions[i] = new TextureRegion(tex);
                delays[i]  = f.delayMs;
                f.pixmap.dispose();
            }

            Log.info("[ADGifProcessor] Loaded " + regions.length + " frames from " + internalPath);
            return new GifAnimation(regions, delays);

        } catch (Exception e) {
            Log.err("[ADGifProcessor] Error loading " + internalPath + ": " + e.getMessage());
            return empty();
        }
    }

    private static GifAnimation empty() {
        return new GifAnimation(new TextureRegion[0], new int[0]);
    }
}