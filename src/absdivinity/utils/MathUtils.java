package absdivinity.utils;

import arc.math.Mathf;
import arc.graphics.Color;

/**
 * Utility class for common mathematical and color operations used throughout the mod.
 * Provides helper methods to reduce code duplication and improve readability.
 *
 * @author AbsDivinity Team
 */
public class MathUtils {

    /**
     * Clamps a value between a minimum and maximum.
     *
     * @param value The value to clamp
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return The clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Calculates distance between two points.
     *
     * @param x1 First point X coordinate
     * @param y1 First point Y coordinate
     * @param x2 Second point X coordinate
     * @param y2 Second point Y coordinate
     * @return The distance between the two points
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return Mathf.sqrt(dx * dx + dy * dy);
    }

    /**
     * Interpolates between two values using linear interpolation.
     *
     * @param start Start value
     * @param end End value
     * @param t Interpolation factor (0 to 1)
     * @return Interpolated value
     */
    public static float lerp(float start, float end, float t) {
        return start + (end - start) * clamp(t, 0f, 1f);
    }

    /**
     * Converts a float value to a packed color integer.
     *
     * @param color The color to pack
     * @return Packed color as float bits
     */
    public static float packColor(Color color) {
        return color.toFloatBits();
    }

    /**
     * Unpacks a float-packed color to a Color object.
     *
     * @param packed The packed color value
     * @return Color object
     */
    public static Color unpackColor(float packed) {
        return new Color().abgr8888(packed);
    }
}