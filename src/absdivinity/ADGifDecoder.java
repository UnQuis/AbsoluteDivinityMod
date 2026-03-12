package absdivinity;

import java.io.InputStream;

import arc.graphics.Pixmap;
import arc.struct.Seq;

public class ADGifDecoder {

    public static class GifFrame {
        public final Pixmap pixmap;
        public final int    delayMs;

        public GifFrame(Pixmap pixmap, int delayMs) {
            this.pixmap   = pixmap;
            this.delayMs  = delayMs;
        }
    }

    public static Seq<GifFrame> decode(InputStream is) {
        Seq<GifFrame> frames = new Seq<>();
        try {
            java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) buf.write(chunk, 0, n);
            byte[] data = buf.toByteArray();
            new Parser(data, frames).parse();
        } catch (Exception e) {
            arc.util.Log.err("[ADGifDecoder] Failed to decode GIF: " + e.getMessage());
        }
        return frames;
    }

    private static class Parser {
        final byte[] data;
        final Seq<GifFrame> out;
        int pos = 0;

        int screenW, screenH;

        int[] globalCT;

        int  delayCs       = 0;
        int  transpIndex   = -1;
        int  disposalMethod = 0;

        int[] canvas;

        Parser(byte[] data, Seq<GifFrame> out) {
            this.data = data;
            this.out  = out;
        }


        void parse() {
            String header = new String(data, 0, 6);
            if (!header.startsWith("GIF")) {
                arc.util.Log.err("[ADGifDecoder] Not a GIF file");
                return;
            }
            pos = 6;

            screenW = readShortLE();
            screenH = readShortLE();
            int packed = readByte();
            int bgIndex = readByte();
            readByte();

            boolean hasGCT  = (packed & 0x80) != 0;
            int     gctSize = 2 << (packed & 0x07);
            globalCT = hasGCT ? readColorTable(gctSize) : new int[0];

            canvas = new int[screenW * screenH];
            if (hasGCT && bgIndex < globalCT.length)
                java.util.Arrays.fill(canvas, globalCT[bgIndex]);

            while (pos < data.length) {
                int block = readByte();
                if (block == 0x3B) break;
                else if (block == 0x2C) readImageDescriptor();
                else if (block == 0x21) readExtension();
            }
        }

        void readExtension() {
            int label = readByte();
            if (label == 0xF9) {
                readGraphicControlExtension();
            } else {
                skipSubBlocks();
            }
        }

        void readGraphicControlExtension() {
            readByte();
            int packed    = readByte();
            disposalMethod = (packed >> 2) & 0x07;
            boolean hasTransp = (packed & 0x01) != 0;
            delayCs      = readShortLE();
            int tIdx     = readByte();
            transpIndex  = hasTransp ? tIdx : -1;
            readByte();
        }

        void readImageDescriptor() {
            int frameX = readShortLE();
            int frameY = readShortLE();
            int frameW = readShortLE();
            int frameH = readShortLE();
            int packed  = readByte();

            boolean hasLCT    = (packed & 0x80) != 0;
            boolean interlaced = (packed & 0x40) != 0;
            int     lctSize   = 2 << (packed & 0x07);

            int[] colorTable = hasLCT ? readColorTable(lctSize) : globalCT;

            int lzwMin = readByte();

            byte[] compressed = readSubBlocks();

            int[] indices = lzwDecompress(compressed, lzwMin, frameW * frameH);

            if (interlaced) indices = deinterlace(indices, frameW, frameH);

            int[] prevCanvas = null;
            if (disposalMethod == 3) {
                prevCanvas = canvas.clone();
            }

            int[] prevFrame = canvas.clone();

            for (int fy = 0; fy < frameH; fy++) {
                for (int fx = 0; fx < frameW; fx++) {
                    int idx  = indices[fy * frameW + fx];
                    int px   = frameX + fx;
                    int py   = frameY + fy;
                    if (px < 0 || px >= screenW || py < 0 || py >= screenH) continue;
                    if (idx == transpIndex) continue;
                    int argb = colorTable[idx];
                    canvas[py * screenW + px] = argb;
                }
            }

            Pixmap pixmap = canvasToPixmap();
            int delayMs   = Math.max(delayCs * 10, 20);
            out.add(new GifFrame(pixmap, delayMs));

            switch (disposalMethod) {
                case 2:
                    clearRect(frameX, frameY, frameW, frameH,
                              (globalCT.length > 0) ? globalCT[0] : 0);
                    break;
                case 3:
                    if (prevCanvas != null) canvas = prevCanvas;
                    break;
            }

            delayCs        = 0;
            transpIndex    = -1;
            disposalMethod = 0;
        }

        int[] lzwDecompress(byte[] input, int minCodeSize, int pixelCount) {
            int[] output = new int[pixelCount];
            int outPos   = 0;

            int clearCode = 1 << minCodeSize;
            int eofCode   = clearCode + 1;

            int tableSize  = clearCode + 2;
            int codeSize   = minCodeSize + 1;
            int codeMask   = (1 << codeSize) - 1;

            int[] prefix = new int[4096];
            int[] suffix = new int[4096];
            for (int i = 0; i < clearCode; i++) {
                prefix[i] = -1;
                suffix[i] = i;
            }

            int bitBuf   = 0;
            int bitCount = 0;
            int bytePos  = 0;

            int prevCode  = -1;
            int firstByte = 0;

            while (bytePos < input.length && outPos < pixelCount) {
                while (bitCount < codeSize && bytePos < input.length) {
                    bitBuf   |= (input[bytePos++] & 0xFF) << bitCount;
                    bitCount += 8;
                }

                int code = bitBuf & codeMask;
                bitBuf   >>= codeSize;
                bitCount -= codeSize;

                if (code == clearCode) {
                    codeSize  = minCodeSize + 1;
                    codeMask  = (1 << codeSize) - 1;
                    tableSize = clearCode + 2;
                    prevCode  = -1;
                    continue;
                }

                if (code == eofCode) break;

                int[]  stack    = new int[4096];
                int    stackTop = 0;
                int    c        = code;

                boolean isNew = (code >= tableSize);
                if (isNew) {
                    stack[stackTop++] = firstByte;
                    c = prevCode;
                }

                while (prefix[c] != -1) {
                    stack[stackTop++] = suffix[c];
                    c = prefix[c];
                }
                stack[stackTop++] = suffix[c];
                firstByte = suffix[c];

                for (int i = stackTop - 1; i >= 0 && outPos < pixelCount; i--) {
                    output[outPos++] = stack[i];
                }

                if (isNew && tableSize < 4096) {
                    prefix[tableSize] = prevCode;
                    suffix[tableSize] = firstByte;
                    tableSize++;
                } else if (prevCode != -1 && tableSize < 4096) {
                    prefix[tableSize] = prevCode;
                    suffix[tableSize] = firstByte;
                    tableSize++;
                }

                prevCode = code;

                if (tableSize > (1 << codeSize) && codeSize < 12) {
                    codeSize++;
                    codeMask = (1 << codeSize) - 1;
                }
            }

            return output;
        }

        static final int[] INTERLACE_START = { 0, 4, 2, 1 };
        static final int[] INTERLACE_INC   = { 8, 8, 4, 2 };

        int[] deinterlace(int[] src, int w, int h) {
            int[] dst = new int[w * h];
            int srcRow = 0;
            for (int pass = 0; pass < 4; pass++) {
                for (int y = INTERLACE_START[pass]; y < h; y += INTERLACE_INC[pass]) {
                    System.arraycopy(src, srcRow * w, dst, y * w, w);
                    srcRow++;
                }
            }
            return dst;
        }

        Pixmap canvasToPixmap() {
            Pixmap pixmap = new Pixmap(screenW, screenH);
            for (int y = 0; y < screenH; y++) {
                for (int x = 0; x < screenW; x++) {
                    int argb = canvas[y * screenW + x];
                    int r   = (argb >> 16) & 0xFF;
                    int g   = (argb >>  8) & 0xFF;
                    int b   =  argb        & 0xFF;
                    int a   = (argb >> 24) & 0xFF;
                    int rgba = (r << 24) | (g << 16) | (b << 8) | a;
                    pixmap.set(x, y, rgba);
                }
            }
            return pixmap;
        }

        void clearRect(int fx, int fy, int fw, int fh, int color) {
            for (int y = fy; y < fy + fh && y < screenH; y++)
                for (int x = fx; x < fx + fw && x < screenW; x++)
                    canvas[y * screenW + x] = color;
        }

        int[] readColorTable(int size) {
            int[] ct = new int[size];
            for (int i = 0; i < size; i++) {
                int r = readByte();
                int g = readByte();
                int b = readByte();
                ct[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
            return ct;
        }

        byte[] readSubBlocks() {
            int savedPos = pos;
            int total = 0;
            while (pos < data.length) {
                int size = data[pos++] & 0xFF;
                if (size == 0) break;
                total += size;
                pos   += size;
            }
            byte[] result = new byte[total];
            pos = savedPos;
            int dst = 0;
            while (pos < data.length) {
                int size = data[pos++] & 0xFF;
                if (size == 0) break;
                System.arraycopy(data, pos, result, dst, size);
                pos += size;
                dst += size;
            }
            return result;
        }

        void skipSubBlocks() {
            while (pos < data.length) {
                int size = data[pos++] & 0xFF;
                if (size == 0) break;
                pos += size;
            }
        }

        int readByte()    { return data[pos++] & 0xFF; }
        int readShortLE() { return (data[pos++] & 0xFF) | ((data[pos++] & 0xFF) << 8); }
    }
}