package com.app.musicplayer.utils.pageindicator.animation.data.type;

import com.app.musicplayer.utils.pageindicator.animation.data.Value;

public class SlideAnimationValue implements Value {

    private int coordinate;

    public int getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(int coordinate) {
        this.coordinate = coordinate;
    }
}
