package me.ionar.salhack.module.render;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.render.EventTransformSideFirstPersonEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;

public class ViewModelModule extends Module {
	//public static Value<Boolean> cancelEating = new Value<Boolean>("NoEat", new String[] {""}, "No Eat.", false);
	public static Value<Double> xLeft = new Value<Double>("Left X", new String[] {""}, "LeftX", 2.0, 0.0, 4.0, 0.1);
	public static Value<Double> yLeft = new Value<Double>("Left Y", new String[] {""}, "LeftY", 2.2, 0.0, 4.0, 0.1);
	public static Value<Double> zLeft = new Value<Double>("Left Z", new String[] {""}, "LeftZ", 1.2, 0.0, 4.0, 0.1);
	public static Value<Double> xRight = new Value<Double>("Right X", new String[] {""}, "RightX", 2.0, 0.0, 4.0, 0.1);
	public static Value<Double> yRight = new Value<Double>("Right Y", new String[] {""}, "RightY", 2.2, 0.0, 4.0, 0.1);
	public static Value<Double> zRight = new Value<Double>("Right Z", new String[] {""}, "RightZ", 1.2, 0.0, 4.0, 0.1);
	
	public ViewModelModule() {
		super("ViewModel", new String[] {""}, "Changes the viewport.", "NONE", -1, ModuleType.RENDER);
	}
	
	@EventHandler
	private final Listener<EventTransformSideFirstPersonEvent> eventListener = new Listener<>(event -> {
		if (event.getHandSide() == EnumHandSide.RIGHT){
			GlStateManager.translate(xRight.getValue() - 2, yRight.getValue() - 2, zRight.getValue() - 2);
		} else if (event.getHandSide() == EnumHandSide.LEFT){
			GlStateManager.translate(xLeft.getValue() - 2, yLeft.getValue() - 2, zLeft.getValue() - 2);
		}
	});
	
	@Override
	public void onEnable(){
		SalHackMod.EVENT_BUS.subscribe(this);
	}

	@Override
	public void onDisable(){
		SalHackMod.EVENT_BUS.unsubscribe(this);
	}
}