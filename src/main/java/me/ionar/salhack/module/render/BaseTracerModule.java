package me.ionar.salhack.module.render;

import java.util.List;

import me.ionar.salhack.events.render.RenderEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.ChunkUtils;
import me.ionar.salhack.util.render.RenderUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

public class BaseTracerModule extends Module{
	public static Value<Boolean> tracePortal = new Value<Boolean>("TracePortal", new String[] {"TP"}, "Traces portals.", true);
	public static Value<Boolean> traceChests = new Value<Boolean>("TraceChests", new String[] {"TC"}, "Traces chests.", true);
	
	public BaseTracerModule() {
		super("BaseTracers", new String[] {"BaseT"}, "Traces a trajectory to a portal/chest.", "NONE", -1, ModuleType.RENDER);
	}
	
	@EventHandler
	private Listener<RenderEvent> onRender = new Listener<>(event -> {
        if (mc.getRenderManager() == null || mc.getRenderManager().options == null) return;
        ChunkProviderClient chunkProvider = (ChunkProviderClient) mc.world.getChunkProvider();
        List<?> chunks = ChunkUtils.stealAndGetField(chunkProvider, List.class);
        for (int currentChunk = 0; currentChunk < chunks.size(); currentChunk++) {
        	Chunk c = (Chunk) chunks.get(currentChunk);
			for (int xx = 0; xx < 16; xx++)
				for (int zz = 0; zz < 16; zz++)
					for (int yy = 0; yy < 256; yy++) {
						Block block = c.getBlockState(xx, yy, zz).getBlock();
						if(getColor(block) != -1) renderBlock(event, block, xx, yy, zz);
					}
        }
	});
	
	public void renderBlock(RenderEvent event, Block block, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
        final boolean bobbing = mc.gameSettings.viewBobbing;
        mc.gameSettings.viewBobbing = false;
        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
        final Vec3d forward = new Vec3d(0, 0, 1).rotatePitch(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationPitch)).rotateYaw(-(float) Math.toRadians(Minecraft.getMinecraft().player.rotationYaw));
        RenderUtil.drawLine3D((float) forward.x, (float) forward.y + mc.player.getEyeHeight(), (float) forward.z, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), 0.5f, getColor(block));
        mc.gameSettings.viewBobbing = bobbing;
        mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);
	}
	
	public int getColor(Block block) {
		if((block instanceof BlockPortal) && tracePortal.getValue())
			return 0x823d85;
		else if((block instanceof BlockChest) && traceChests.getValue())
			return 0xFEE11A;
		return -1;
	}
}