package me.ionar.salhack.module.render;

import java.awt.Color;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkyColorModule extends Module {
	public final Value<Integer> r = new Value<Integer>("Red", new String[] {"R"}, "Color red.", 255, 0, 255, 5);
	public final Value<Integer> g = new Value<Integer>("Green", new String[] {"G"}, "Green color.", 0, 0, 255, 5);
	public final Value<Integer> b = new Value<Integer>("Blue", new String[] {"B"}, "Blue color.", 255, 0, 255, 5);
	
	public SkyColorModule() {
		super("SkyColor", new String[] {"SKYC"}, "Changes the color of the sky.", "NONE", 0xDB3C24, ModuleType.RENDER);
	}
    public int getSkyColorByTemp(final float par1) {
        return Color.red.getRGB();
    }
    
    @SubscribeEvent
    public void fogColors(final EntityViewRenderEvent.FogColors event) {
        event.setRed(this.r.getValue() / 255.0f);
        event.setGreen(this.g.getValue() / 255.0f);
        event.setBlue(this.b.getValue() / 255.0f);
    }
    
    
    @SubscribeEvent
    public void fogDensity(final EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0.0f);
        event.setCanceled(true);
    }
    
    @Override
    public void onEnable() {
    	MinecraftForge.EVENT_BUS.register((Object)this);
    }
    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
}