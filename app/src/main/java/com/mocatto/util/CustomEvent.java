package com.mocatto.util;

/**
 * Created by froilan.ruiz on 8/30/2016.
 */
import com.p_v.flexiblecalendar.entity.Event;

/**
 * @author p-v
 */
public class CustomEvent implements Event {

    private int color;

    public CustomEvent(int color){
        this.color = color;
    }

    @Override
    public int getColor() {
        return color;
    }
}
