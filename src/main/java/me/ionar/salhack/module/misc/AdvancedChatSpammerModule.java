package me.ionar.salhack.module.misc;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.DirectoryManager;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class AdvancedChatSpammerModule extends Module{
	public static Value<Type> type = new Value<Type>("Type", new String[] {"T"}, "Type of the chat spammer.", Type.PACKET);
	public static enum Type {
		PACKET("PACKET",0),
		CLIENT("CLIENT",1);
		
		private int id;
		private String name;
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		private Type(String name, int id) {
			this.id = id;
			this.name = name;
		}
	};
	public static Value<Mode> mode = new Value<Mode>("Mode", new String[] {"M"}, "Mode of the chat spammer.", Mode.INSULTER);
	public static enum Mode {
		INSULTER("INSULTER", 0),
		SPAMMER("SPAMMER", 1);
		//JOINLEAVE(2);
		private int id;
		private String name;
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		private Mode(String name, int id) {
			this.id = id;
			this.name = name;
		}
	};
	public static Value<Double> delay = new Value<Double>("Delay", new String[] {"D"}, "Delay of chat spammer.", 6.0, 0.5, 60.0, 0.5);
	
	
	public static ArrayList<String[]> messages = new ArrayList<String[]>();
	
	public AdvancedChatSpammerModule() {
		super("AdvancedChatSpammer", new String[] {"ACS"}, "An advanced chat spammer.", "NONE", 0xDB24C4, ModuleType.MISC);
	}
	
	public static double getMesuredTicks(double sec) {return 20.0 * sec;}
	public static String[] getMessages() {
		return messages.get(mode.getValue().getId());
	}
	static Random random = new Random();
	public static String getMessage() {
		String[] msgs = getMessages();
		return msgs[random.nextInt(msgs.length+1)];
	}
	public static void runMessage() {
		String message = "";
		switch (mode.getValue().getName()) {
			case "INSULTER":
			{
				message = getMessage();
				if(Minecraft.getMinecraft().isSingleplayer()) message = message.replaceAll("%player%", Minecraft.getMinecraft().player.getName());
				else while(message.contains("%player%")) {
					List<EntityPlayerMP> players = Minecraft.getMinecraft().player.getServer().getPlayerList().getPlayers();
					message = message.replaceFirst("%player%", players.get(random.nextInt(players.size()+1)).getName());
				}
				break;
			}
			case "SPAMMER":{
				message = getMessage();
				if(Minecraft.getMinecraft().isSingleplayer()) message = message.replaceAll("%player%", Minecraft.getMinecraft().player.getName());
				else while(message.contains("%player%")) {
					List<EntityPlayerMP> players = Minecraft.getMinecraft().player.getServer().getPlayerList().getPlayers();
					message = message.replaceFirst("%player%", players.get(random.nextInt(players.size()+1)).getName());
				}
				break;
			}
		}
		
		switch (type.getValue().getName()) {
		case "PACKET":
			Minecraft.getMinecraft().getConnection().sendPacket(new CPacketChatMessage(message));
			break;
		case "CLIENT":
			Minecraft.getMinecraft().player.sendChatMessage(message);
			break;
		}
	}
	
	
	public static int ticks = 0;
	public static void tickClient() {

	}
	
	/*@EventHandler
	private Listener<ClientTickEvent> eventTick = new Listener<>(p_Event -> {
		if(!ModuleManager.Get().GetMod(AdvancedChatSpammerModule.class).isEnabled()) return;
		if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) return;
		double mesuredTicks = getMesuredTicks(delay.getValue());
		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Tick #" + ticks + "!"));
		ticks++;
		if(ticks >= mesuredTicks) {
			runMessage();
			ticks=0;
		}
	});*/
	
	public String loadDefaults(String fileName) throws Exception{
		String content = "";
		switch (fileName) {
		case "AdvancedChatSpammer/insulter.txt":
			content+=""
					+ "%player% yo cunt pk2client is better than future.\n"
					+ "%player% you cuck\n"
					+ "%player% dont abouse\n"
					+ "%player% is my step dad\n"
					+ "%player% again daddy!\n"
					+ "dont worry %player% it happens to every one\n"
					+ "What are you, fucking gay, %player%?\n"
					+ "Did you know? %player% hates you, %player%\n"
					+ "You are literally 10, %player%\n"
					+ "%player% finally lost their virginity, sadly they lost it to %player%... yeah, that's unfortunate.\n"
					+ "%player%, don't be upset, it's not like anyone cares about you, fag.\n"
					+ "%player%, see that rubbish bin over there? Get your ass in it, or I'll get %player% to whoop your ass.\n"
					+ "%player%, may I borrow that dirt block? that guy named %player% needs it...\n"
					+ "Yo, %player%, btfo you virgin\n"
					+ "Hey %player% want to play some High School RP with me and %player%?\n"
					+ "%player% is an Archon player. Why is he on here? Fucking factions player.\n"
					+ "Did you know? %player% just joined The Vortex Coalition!\n"
					+ "%player% has successfully conducted the cactus dupe and duped a itemhand!\n"
					+ "%player% has successfully conducted the lava dupe and duped a itemhand!\n"
					+ "%player%, are you even human? You act like my dog, holy shit.\n"
					+ "%player%, you were never loved by your family.\n"
					+ "Come on %player%, you hurt %player%'s feelings. You meany.\n"
					+ "Stop trying to meme %player%, you can't do that. kek\n"
					+ "%player% is gay. Don't go near him.\n"
					+ "Whoa %player% didn't mean to offend you, %player%.";
			break;
		case "AdvancedChatSpammer/spammer.txt":
			content+=""
					+ "%player% PK2Client is the best client.\n"
					+ "Configure this in .minecraft/PK2Client/AdvancedChatSpammer/spammer.txt";
			break;
		case "AdvancedChatSpammer/joinleave.txt":
			content+=""
					+ "join:Welcome %player% to this server!\n"
					+ "join:L00k %player% has joined the server! Everyone stop swearing.\n"
					+ "join:%player% has joined the party, but the party ended.\n"
					+ "join:Hello %player%\n"
					+ "join:%player% joined, give him kits.\n"
					+ "join:%player% joined, introduce him to this server.\n"
					+ "join:Hey %player%!\n"
					+ "leave:Bye %player%\n"
					+ "leave:%player% left, now we can continue the party.\n"
					+ "leave:%player% left.\n"
					+ "leave:%player% is gay and left.\n"
					+ "leave:%player% left this gae server.";
			break;
		default:
			break;
		}
		FileWriter writer = new FileWriter(DirectoryManager.Get().GetCurrentDirectory() + "/PK2Client/" + fileName);
		writer.write(content);
		writer.close();
		return content;
	}
	public void writeFile(String fileName, String content) throws Exception {
		FileWriter writer = new FileWriter(DirectoryManager.Get().GetCurrentDirectory() + "/PK2Client/" + fileName);
		writer.write(content);
		writer.close();
	}
	public String readFile(String fileName) throws Exception {
		String data = "";
		File myObj = new File(DirectoryManager.Get().GetCurrentDirectory() + "/PK2Client/" + fileName);
	    try {
	        
	        Scanner scanner = new Scanner(myObj);
	        while (scanner.hasNextLine()) {
	          data+=scanner.nextLine();
	          if(scanner.hasNextLine()) data+="\n";
	        }
	        scanner.close();
	      } catch (Exception e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	        
	        new File(DirectoryManager.Get().GetCurrentDirectory() + "/PK2Client/AdvancedChatSpammer").mkdirs();
	        myObj.createNewFile();
	        return loadDefaults(fileName);
	      }
		return data;
	}
	public void onJoin(EntityPlayer player) {
		//if(mode.getValue() != Mode.JOINLEAVE) return;
		String[] msgs = getMessages();
		ArrayList<String> joinMsgs = new ArrayList<String>();
		for(String msg : msgs)
			if(msg.startsWith("join:"))
				joinMsgs.add(msg.replaceFirst("join:", ""));
		String msg = joinMsgs.get(new Random().nextInt(joinMsgs.size()+1));
		Minecraft.getMinecraft().player.sendChatMessage(msg.replaceAll("%player%", player.getName()));
	}
	public void onLeave(EntityPlayer player) {
		//if(mode.getValue() != Mode.JOINLEAVE) return;
		String[] msgs = getMessages();
		ArrayList<String> joinMsgs = new ArrayList<String>();
		for(String msg : msgs)
			if(msg.startsWith("leave:"))
				joinMsgs.add(msg.replaceFirst("leave:", ""));
		String msg = joinMsgs.get(new Random().nextInt(joinMsgs.size()+1));
		Minecraft.getMinecraft().player.sendChatMessage(msg.replaceAll("%player%", player.getName()));
	}
    public void AdvancedChatSpammerModuleTick() {
		if(!ModuleManager.Get().GetMod(AdvancedChatSpammerModule.class).isEnabled()) return;
		if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) return;
		AdvancedChatSpammerModule.runMessage();
		/*double mesuredTicks = AdvancedChatSpammerModule.getMesuredTicks(AdvancedChatSpammerModule.delay.getValue());
		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Tick #" + ticks + "!"));
		AdvancedChatSpammerModule.ticks++;
		if(AdvancedChatSpammerModule.ticks >= mesuredTicks) {
			AdvancedChatSpammerModule.runMessage();
			AdvancedChatSpammerModule.ticks=0;
		}*/
    }
	Timer timer;
	TimerTask currentTask;
	
	@Override
	public void onEnable() {
		messages.clear();
		try {
			messages.add(readFile("AdvancedChatSpammer/insulter.txt").split("\n")); // INSULTER \\
			messages.add(readFile("AdvancedChatSpammer/spammer.txt").split("\n")); // SPAMMER \\
			messages.add(readFile("AdvancedChatSpammer/joinleave.txt").split("\n")); // JOINLEAVE \\
			
		} catch(Exception e) {e.printStackTrace(); if(isEnabled()) toggle(); return;}
		timer = new Timer();
		currentTask = new TimerTask() {
			@Override
			public void run() {
				AdvancedChatSpammerModuleTick();
			}
		};
		final double delay = AdvancedChatSpammerModule.delay.getValue()*1000;
		timer.schedule(currentTask, 10, (int)delay);
	}
	@Override
	public void onDisable() {
		currentTask.cancel();
		timer.cancel();
		timer = null;
		currentTask = null;
	}
}