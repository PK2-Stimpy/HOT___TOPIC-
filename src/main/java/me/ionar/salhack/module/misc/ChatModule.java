package me.ionar.salhack.module.misc;

import me.ionar.salhack.events.player.EventPlayerSendChatMessage;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

public class ChatModule extends Module{
	public ChatModule() {
		super("Chat", new String[] {"CHAT"}, "Applies all changes to chat events. (Suffix...)", "NONE", 0xDB24C4, ModuleType.MISC);
	}
	
	public ChatSuffixModule _ChatSuffixModule;
	public L2X9ColoredChatModule _L2X9ColoredChatModule;
	public String[] exceptions = {"/",".",";"};
	
	@EventHandler
	private Listener<EventPlayerSendChatMessage> onChatMessage = new Listener<>(p_Event -> {
		_ChatSuffixModule = (ChatSuffixModule)ModuleManager.Get().GetMod(ChatSuffixModule.class);
		_L2X9ColoredChatModule = (L2X9ColoredChatModule)ModuleManager.Get().GetMod(L2X9ColoredChatModule.class);
		for(String exception : exceptions) if(p_Event.Message.startsWith(exception.toLowerCase())) return;
		p_Event.cancel();
		String l_msg = p_Event.Message;
		if(_L2X9ColoredChatModule.isEnabled()) {
	        String m_prefix = "";
	        switch (_L2X9ColoredChatModule.Color.getValue()) {
			case GREEN:
				m_prefix = ">";
				break;
			case BLUE:
				m_prefix = "``";
				break;
			case YELLOW:
				m_prefix = "#";
				break;
			}
	        l_msg = m_prefix + l_msg;
		}
		if(_ChatSuffixModule.isEnabled()) l_msg+=" » "+_ChatSuffixModule.Suffix.getValue();
		mc.getConnection().sendPacket(new CPacketChatMessage(l_msg));
	}); 
}