package me.ionar.salhack.module.render;

import java.util.Iterator;

import me.ionar.salhack.events.entity.EventEntityAdded;
import me.ionar.salhack.events.entity.EventEntityRemoved;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;

public class MobOwnerModule extends Module{
	public MobOwnerModule() {
		super("MobOwner", new String[] {"MOBO"}, "Allows you to see who originally tamed an entity.", "NONE", 0xDB3C24, ModuleType.RENDER);
	}
	
	private void eAdded(Entity entity) {
    	if(entity instanceof EntityTameable) {
    		EntityTameable tameable = (EntityTameable)entity;
    		if (!(tameable.isTamed() && tameable.getOwner() != null)) return;
    		
    		tameable.setAlwaysRenderNameTag(true);
    		tameable.setCustomNameTag("Owner: " + tameable.getOwner().getDisplayName().getFormattedText());
    	}
    	if(entity instanceof AbstractHorse) {
    		AbstractHorse tameable = (AbstractHorse)entity;
    		if (!(tameable.isTame() && tameable.getOwnerUniqueId() != null)) return;
    		
    		tameable.setAlwaysRenderNameTag(true);
    		tameable.setCustomNameTag("OwnerUUID: " + tameable.getOwnerUniqueId().toString());
    	}
	}
	
	private void eRemoved(Entity entity) {
		if(!(entity instanceof EntityTameable) && !(entity instanceof AbstractHorse)) return;
		try {
			entity.setAlwaysRenderNameTag(false);
		} catch(Exception e) {e.printStackTrace();}
	}
	
	@Override
	public void onEnable() {
		try {
	        final Iterator<Entity> iterator = Wrapper.GetMC().world.loadedEntityList.iterator();
	        if (iterator.hasNext()) eAdded(iterator.next());
		} catch(Exception e) {}
	}
	
	@Override
	public void onDisable() {
		try {
	        final Iterator<Entity> iterator = Wrapper.GetMC().world.loadedEntityList.iterator();
	        if (iterator.hasNext()) eRemoved(iterator.next());
		} catch(Exception e) {}
	}
	

	@EventHandler
    private Listener<EventEntityAdded> OnEntityAdded = new Listener<>(p_Event -> {
    	eAdded(p_Event.GetEntity());
    });
	
	@EventHandler
	private Listener<EventEntityRemoved> OnEntityRemoved = new Listener<>(p_Event -> {
		eRemoved(p_Event.GetEntity());
	});
}