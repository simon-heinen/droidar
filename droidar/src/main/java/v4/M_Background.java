package v4;

public class M_Background {
    private final int color;
    private final int strokeColor;
    private final int strokeWidth;
    private final float cornerRadiusDp;

    public M_Background(int color, int strokeColor, int strokeWidth, float cornerRadiusDp) {
        this.color = color;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.cornerRadiusDp = cornerRadiusDp;
    }

    public int getColor() {
        return color;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public float getCornerRadiusDp() {
        return cornerRadiusDp;
    }
}
