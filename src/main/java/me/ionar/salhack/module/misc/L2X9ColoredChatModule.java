package me.ionar.salhack.module.misc;

import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public class L2X9ColoredChatModule extends Module{
	public Value<Colors> Color = new Value<Colors>("Color", new String[] {"C"}, "Color to apply to messages.", Colors.BLUE);
	public enum Colors {
		GREEN, // >
		BLUE, // ``
		YELLOW // #
	};
	
	public ChatModule _ChatModule;
	
	public L2X9ColoredChatModule() {
		super("L2X9ChatColor", new String[] {"L2X9CC"}, "Applies the color to every message you send on L2X9.", "NONE", 0xDB24C4, ModuleType.MISC);
	}
	@Override
	public void onEnable() {
		_ChatModule = (ChatModule)ModuleManager.Get().GetMod(ChatModule.class);
		if(!_ChatModule.isEnabled()) _ChatModule.toggle();
	}
	@Override
	public String getMetaData() {
		return Color.getValue().toString();
	}
}