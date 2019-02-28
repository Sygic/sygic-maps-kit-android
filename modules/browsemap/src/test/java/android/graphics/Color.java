package android.graphics;

import androidx.annotation.IntRange;
import org.jetbrains.annotations.TestOnly;

public class Color {

    @TestOnly
    public static int argb(
            @IntRange(from = 0, to = 255) final int alpha,
            @IntRange(from = 0, to = 255) final int red,
            @IntRange(from = 0, to = 255) final int green,
            @IntRange(from = 0, to = 255) final int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}