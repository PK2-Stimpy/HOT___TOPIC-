package me.ionar.salhack.module.combat;

import java.util.List;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.render.ChatColor;
import net.minecraft.entity.player.EntityPlayer;

public class AutoLavaModule extends Module {
	public static final Value<Integer> range = new Value<Integer>("Range", new String[] {"RNG"}, "Range of block placement.", 5, 0, 10, 1);
	
	public AutoLavaModule() {
		super("AutoLava", new String[] {"ALAVA"}, "It places lava on the head of the target.", "NONE", 0x24CADB, ModuleType.COMBAT);
	}
	
    private EntityPlayer findTarget() {
        List<EntityPlayer> playerList = (mc.player.world).playerEntities;
        EntityPlayer closestTarget = null;
        
        for (EntityPlayer target : playerList) {
            if (target == mc.player) {
            	continue;
            }
            if (FriendManager.Get().IsFriend(target.getName())) {
            	continue;
            }
            if (!EntityUtil.isLiving(target)) {
            	continue;
            }
            if (target.getHealth() <= 0.0F) {
            	continue;
            }
            double currentDistance = mc.player.getDistance(target);
            if (currentDistance > ((Double)range.getValue().doubleValue())) {
            	continue;
            }
            if (closestTarget == null) {
            	continue;
            }
            if (currentDistance >= mc.player.getDistance(closestTarget)) {
            	continue;
            }
            closestTarget = target;
        }
        return closestTarget;
    }
	
	@Override
	public void onEnable() {
		EntityPlayer target = findTarget();
		if(target == null) {
			SalHack.SendMessage(ChatColor.RED + "There isn't any close target.");
			toggle();
			return;
		}
		
	}
}