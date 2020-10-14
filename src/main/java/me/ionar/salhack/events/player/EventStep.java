package me.ionar.salhack.events.player;

import me.ionar.salhack.events.MinecraftEvent;

public class EventStep extends MinecraftEvent
{
    private final Time time;
    private float height;

    public EventStep(float height)
    {
        this.time = Time.BEFORE;
        this.height = height;
    }

    public EventStep(Time time)
    {
        this.time = time;
    }

    public Time getTime()
    {
        return time;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public enum Time
    {
        BEFORE, AFTER
    }
}