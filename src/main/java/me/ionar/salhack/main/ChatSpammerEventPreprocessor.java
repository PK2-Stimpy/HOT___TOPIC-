package me.ionar.salhack.main;

import me.ionar.salhack.module.misc.AdvancedChatSpammerModule;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ChatSpammerEventPreprocessor {
	@SubscribeEvent
	public void tickEvent(ClientTickEvent event) {
        if (Wrapper.GetMC().player == null)
            return;
        AdvancedChatSpammerModule.tickClient();
	}
}