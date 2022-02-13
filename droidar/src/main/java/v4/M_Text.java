package v4;

import android.graphics.Typeface;

public class M_Text {
    private final String text;
    private final Typeface font;
    private final float size;
    private final int color;

    private M_Background background;

    public M_Text(String text, Typeface font, float size, int color, M_Background background) {
        this.text = text;
        this.font = font;
        this.size = size;
        this.color = color;
        this.background = background;
    }

    public M_Text(String text, Typeface font, float size, int color) {
        this.text = text;
        this.font = font;
        this.size = size;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Typeface getFont() {
        return font;
    }

    public float getSize() {
        return size;
    }

    public int getColor() {
        return color;
    }

    public M_Background getBackground() {
        return background;
    }

    public void setBackground(M_Background background) {
        this.background = background;
    }
}
