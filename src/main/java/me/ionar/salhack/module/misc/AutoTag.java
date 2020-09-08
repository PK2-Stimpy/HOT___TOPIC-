package me.ionar.salhack.module.misc;

import java.util.ArrayList;
import java.util.Comparator;

import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemNameTag;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class AutoTag extends Module{
	public static Value<Boolean> withersOnly = new Value<Boolean>("WithersOnly", new String[] {"WO"}, "Use nametags only on withers.", false);
	public static Value<Integer> Radius = new Value<Integer>("Radius", new String[] {"R"}, "Radius to search for entities.", 4, 0, 10, 1);
	
	public AutoTag() {
		super("AutoTag", new String[] {"AT"}, "Uses a tag to every entity on your range.", "NONE", 0xDB24C4, ModuleType.MISC);
	}
	
	public boolean isEntityGood(Entity entity) {
		boolean distanceCheck = entity.getDistance(entity) <= Radius.getValue();
		boolean arrayCheck = entities.contains(entity);
		if(withersOnly.getValue()) if(entity instanceof EntityWither) return distanceCheck && arrayCheck; else return false;
		if((entity instanceof EntityPlayer) || (entity instanceof EntityPlayerSP) || (entity instanceof EntityPlayerMP) || 
			(entity instanceof EntityVillager)) return false;
		return distanceCheck && arrayCheck;
	}
	
	public ArrayList<Entity> entities = null;
	
	@Override
	public void onEnable() {
		if(entities == null) entities = new ArrayList<Entity>();
		entities.clear();
	}
	
	@EventHandler
	public Listener<EventClientTick> onUpdate = new Listener<>(event -> {
		if(!(mc.player.getHeldItemMainhand().getItem() instanceof ItemNameTag)) {
			int l_Slot = -1;
            for (int l_I = 0; l_I < 9; ++l_I)
            {
                if (mc.player.inventory.getStackInSlot(l_I).getItem() instanceof ItemNameTag)
                {
                    l_Slot = l_I;
                    mc.player.inventory.currentItem = l_Slot;
                    mc.playerController.updateController();
                    break;
                }
            }
            if(l_Slot == -1 && isEnabled()) toggle();
		}
		Entity l_TargetToHit = mc.world.loadedEntityList.stream()
                .filter(p_Entity -> isEntityGood(p_Entity))
                .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                .orElse(null);
		if(l_TargetToHit == null) return;
		entities.add(l_TargetToHit);
        mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(l_TargetToHit.getPosition(), EnumFacing.UP,
                mc.player.getHeldItemOffhand().getItem() == Items.NAME_TAG ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
	});
}