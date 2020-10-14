package me.ionar.salhack.module.world;

import me.ionar.salhack.events.entity.EventEntityAdded;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityLlama;

public class DonkeyLoggerModule extends Module {
	public static final Value<Boolean> logDonkey = new Value<Boolean>("Donkeys", new String[] {"donkey"}, "Log donkeys.", true);
	public static final Value<Boolean> logLlama = new Value<Boolean>("Llamas", new String[] {"llama"}, "Log llamas.", true);

	public DonkeyLoggerModule() {
		super("DonkeyLogger", new String[] {"donkeylog"}, "Prints in chat when a llama/donkey appears.", "NONE", -1, ModuleType.WORLD);
	}
	
    @EventHandler
    private Listener<EventEntityAdded> OnEntityAdded = new Listener<>(event -> {
    	Entity entity = event.GetEntity();
		if((entity instanceof EntityLlama) && logLlama.getValue()) {
			EntityLlama llama = (EntityLlama)entity;
			SendMessage(String.format("Found llama at [%s, %s] with force %s", String.valueOf(llama.posX), String.valueOf(llama.posZ), String.valueOf(llama.getInventoryColumns())));
		} else if((entity instanceof EntityDonkey) && logDonkey.getValue()) {
			EntityDonkey donkey = (EntityDonkey)entity;
			SendMessage(String.format("Found donkey at [%s, %s]", String.valueOf(donkey.posX), String.valueOf(donkey.posZ)));
		}
	});
}
