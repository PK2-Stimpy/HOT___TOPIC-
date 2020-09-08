package me.ionar.salhack.module.misc;

import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public class ChatSuffixModule extends Module{
	public final Value<String> Suffix = new Value<String>("Suffix", new String[] {"S"}, "Suffix to apply to messages.", "HOT___TOPIC+");
	
	public ChatModule _ChatModule;
	
	public ChatSuffixModule() {
		super("ChatSuffix", new String[] {"CSFFX"}, "Applies a suffix to every message you send.", "NONE", 0xDB24C4, ModuleType.MISC);
	}
	@Override
	public void onEnable() {
		_ChatModule = (ChatModule)ModuleManager.Get().GetMod(ChatModule.class);
		if(!_ChatModule.isEnabled()) _ChatModule.toggle();
	}
	@Override
	public String getMetaData() {
		return Suffix.getValue();
	}
}