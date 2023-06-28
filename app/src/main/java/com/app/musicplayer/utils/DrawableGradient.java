package com.app.musicplayer.utils;

import android.graphics.drawable.GradientDrawable;

public class DrawableGradient extends GradientDrawable {
    public DrawableGradient(int[] colors, int cornerRadius) {
        super(GradientDrawable.Orientation.TOP_BOTTOM, colors);

        try {
            this.setShape(GradientDrawable.RECTANGLE);
            this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            //this.setCornerRadius(cornerRadius);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DrawableGradient SetTransparency(int transparencyPercent) {
        this.setAlpha(255 - ((255 * transparencyPercent) / 100));

        return this;
    }
}
