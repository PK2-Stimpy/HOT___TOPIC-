package me.ionar.salhack.module.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.ionar.salhack.events.player.EventPlayerUpdate;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.util.PhobosPlayerUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

public class MobOwnerModule extends Module{
    private Map<Entity, String> owners = new HashMap<Entity, String>();
    private Map<Entity, UUID> toLookUp = new ConcurrentHashMap<Entity, UUID>();
    private List<Entity> lookedUp = new ArrayList<Entity>();
	
	public MobOwnerModule() {
		super("MobOwner", new String[] {"MOBO"}, "Shows you who owns mobs.", "NONE", 0xDB3C24, ModuleType.RENDER);
	}
	
	@EventHandler
	private Listener<EventPlayerUpdate> OnPlayerUpdate = new Listener<>(p_Event -> {
		if (PhobosPlayerUtil.timer.passed(5000)) {
			if(!this.isEnabled()) return;
            for (final Map.Entry<Entity, UUID> entry : toLookUp.entrySet()) {
                final Entity entity = entry.getKey();
                final UUID uuid = entry.getValue();
                if (uuid != null) {
                    final EntityPlayer owner = Wrapper.GetMC().world.getPlayerEntityByUUID(uuid);
                    if (owner == null) {
                        try {
                            final String name = PhobosPlayerUtil.getNameFromUUID(uuid);
                            if (name != null) {
                                this.owners.put(entity, name);
                                this.lookedUp.add(entity);
                            }
                        }
                        catch (Exception e) {
                            this.lookedUp.add(entity);
                            this.toLookUp.remove(entry);
                        }
                        PhobosPlayerUtil.timer.reset();
                        break;
                    }
                    this.owners.put(entity, owner.getName());
                    this.lookedUp.add(entity);
                }
                else {
                    this.lookedUp.add(entity);
                    this.toLookUp.remove(entry);
                }
            }
        }
        for (final Entity entity2 : Wrapper.GetMC().world.getLoadedEntityList()) {
            if (!entity2.getAlwaysRenderNameTag()) {
                if (entity2 instanceof EntityTameable) {
                    final EntityTameable tameableEntity = (EntityTameable)entity2;
                    if (!tameableEntity.isTamed() || tameableEntity.getOwnerId() == null) {
                        continue;
                    }
                    if (this.owners.get(tameableEntity) != null) {
                        tameableEntity.setAlwaysRenderNameTag(true);
                        tameableEntity.setCustomNameTag((String)this.owners.get(tameableEntity));
                    }
                    else {
                        if (this.lookedUp.contains(entity2)) {
                            continue;
                        }
                        this.toLookUp.put((Entity)tameableEntity, tameableEntity.getOwnerId());
                    }
                }
                else {
                    if (!(entity2 instanceof AbstractHorse)) {
                        continue;
                    }
                    final AbstractHorse tameableEntity2 = (AbstractHorse)entity2;
                    if (!tameableEntity2.isTame() || tameableEntity2.getOwnerUniqueId() == null) {
                        continue;
                    }
                    if (this.owners.get(tameableEntity2) != null) {
                        tameableEntity2.setAlwaysRenderNameTag(true);
                        tameableEntity2.setCustomNameTag((String)this.owners.get(tameableEntity2));
                    }
                    else {
                        if (this.lookedUp.contains(entity2)) {
                            continue;
                        }
                        this.toLookUp.put((Entity)tameableEntity2, tameableEntity2.getOwnerUniqueId());
                    }
                }
            }
        }
	});
	
	@Override
	public void onDisable() {
        for (final Entity entity : Wrapper.GetMC().world.loadedEntityList) {
            if (!(entity instanceof EntityTameable)) {
                if (!(entity instanceof AbstractHorse)) {
                    continue;
                }
            }
            try {
                entity.setAlwaysRenderNameTag(false);
            }
            catch (Exception ex) {}
        }
	}
}