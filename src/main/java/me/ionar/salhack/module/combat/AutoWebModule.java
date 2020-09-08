package me.ionar.salhack.module.combat;

import java.util.List;

import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.entity.EntityUtil;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWebModule extends Module{
	public static final Value<Boolean> rotate = new Value<Boolean>("Rotate", new String[] {"R"}, "Rotate when placing.", true);
	public static final Value<Boolean> spoofRotations = new Value<Boolean>("SpoofRotations", new String[] {"SR"}, "Spoof rotations.", true);
	public static final Value<Boolean> spoofHotbar = new Value<Boolean>("SpoofHotbar", new String[] {"SH"}, "Spoof the hotbar.", false);
	public static final Value<Integer> range = new Value<Integer>("Range", new String[] {"RNG"}, "Range of block placement.", 5, 0, 10, 1);
	public static final Value<Integer> bpt = new Value<Integer>("BlocksPerTick", new String[] {"BPT"}, "Blocks placed per tick.", 8, 1, 15, 1);
	
    public AutoWebModule() {
		super("AutoWeb", new String[] {"AW"}, "Traps players with web.", "NONE", 0x24CADB, ModuleType.COMBAT);
	}
	private final Vec3d[] offsetList = {  new Vec3d(0.0D, 1.0D, 0.0D), new Vec3d(0.0D, 0.0D, 0.0D) };
    private boolean slowModeSwitch = false;
    private int playerHotbarSlot = -1; 
    private EntityPlayer closestTarget;
    private int lastHotbarSlot = -1;
    private int offsetStep = 0;
    int blocksPlaced;
    
    @Override
    public String getMetaData() {
    	if(closestTarget != null) return closestTarget.getName();
    	return null;
    }
    
    @EventHandler
    private Listener<EventClientTick> onUpdate = new Listener<>(event -> {
    	if (this.closestTarget == null) {
            return;
        }
        if (this.slowModeSwitch) {
            this.slowModeSwitch = false;
            return;
        }
        for (int i = 0; i < (int)Math.floor(((Double)bpt.getValue().doubleValue())); i++) {
            if (this.offsetStep >= this.offsetList.length) {
                endLoop();
                return;
            }
            Vec3d offset = this.offsetList[this.offsetStep];
            placeBlock((new BlockPos(this.closestTarget.getPositionVector())).down().add(offset.x, offset.y, offset.z));
            this.offsetStep++;
        }
        this.slowModeSwitch = true;
    });
    
    private void placeBlock(BlockPos blockPos) {
        if (!mc.player.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return;
        }
        if (!BlockInteractionHelper.checkForNeighbours(blockPos)) {
            return;
        }
        placeBlockExecute(blockPos);
    }

    public void placeBlockExecute(BlockPos pos) {
        Vec3d eyesPos = new Vec3d((mc.player).posX, (mc.player).posY + mc.player.getEyeHeight(), (mc.player).posZ);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();
            if (!BlockInteractionHelper.canBeClicked(neighbor)) {
            	// REMOVED DEBUG MODE.
            } else {

                Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
                if (eyesPos.squareDistanceTo(hitVec) > 18.0625D) {
                } else {

                    if (((Boolean)spoofRotations.getValue())) {
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    }
                    boolean needSneak = false;
                    Block blockBelow = mc.world.getBlockState(neighbor).getBlock();
                    if (BlockInteractionHelper.blackList.contains(blockBelow) || BlockInteractionHelper.shulkerList.contains(blockBelow)) {
                        needSneak = true;
                    }
                    if (needSneak) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    int obiSlot = findObiInHotbar();
                    if (obiSlot == -1) {
                        if(isEnabled()) toggle();
                        return;
                    }
                    if (this.lastHotbarSlot != obiSlot) {
                        if (((Boolean)spoofHotbar.getValue())) {
                            mc.player.connection.sendPacket(new CPacketHeldItemChange(obiSlot));
                        } else {

                            (mc.player).inventory.currentItem = obiSlot;
                        }
                        this.lastHotbarSlot = obiSlot;
                    }
                    mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    if (needSneak) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }
                    return;
                }
            }
        }
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = (mc.player).inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY &&
                    stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock)stack.getItem()).getBlock();
                if (block instanceof net.minecraft.block.BlockWeb) {
                    slot = i;

                    break;
                }
            }
        }
        return slot;
    }

    private void findTarget() {
        List<EntityPlayer> playerList = (mc.player.world).playerEntities;
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
            if (this.closestTarget == null) {
                this.closestTarget = target;
                continue;
            }
            if (currentDistance >= mc.player.getDistance(this.closestTarget)) {
                continue;
            }
            this.closestTarget = target;
        }
    }


    private void endLoop() {
        this.offsetStep = 0;
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
            mc.player.inventory.currentItem = this.playerHotbarSlot;
            this.lastHotbarSlot = this.playerHotbarSlot;
        }
        findTarget();
    }
    
    @Override
    public void onEnable() {
    	if(mc.player == null && isEnabled()) {toggle();return;}
    	playerHotbarSlot = mc.player.inventory.currentItem;
    	lastHotbarSlot = -1;
    	findTarget();
    }
    @Override
    public void onDisable() {
    	if(mc.player == null) return;
        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            if (((Boolean)spoofHotbar.getValue())) mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
            else mc.player.inventory.currentItem = this.playerHotbarSlot;
        }
        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }
}