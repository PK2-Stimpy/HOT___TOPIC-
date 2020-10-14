package me.ionar.salhack.module.movement;

import java.math.RoundingMode;
import java.util.Objects;
import java.math.BigDecimal;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.MinecraftEvent.Era;
import me.ionar.salhack.events.player.EventPlayerMotionUpdate;
import me.ionar.salhack.events.player.EventPlayerMove;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.EntityUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StrafePhobosModule extends Module {
	public static final Value<Mode> mode = new Value<Mode>("Mode", new String[] {"mode"}, "The current mode for Strafe.", Mode.NCP);
	public static final Value<Boolean> limiter = new Value<Boolean>("SetGround", new String[] {"setground"}, "SetGround.", true);
	public static final Value<Boolean> limiter2 = new Value<Boolean>("Bhop", new String[] {"bhop"}, "Bhop.", false);
	public static final Value<Integer> specialMoveSpeed = new Value<Integer>("Speed", new String[] {"speed"}, "Speed.", 100, 0, 150, 10);
	public static final Value<Integer> potionSpeed = new Value<Integer>("Speed1", new String[] {"speed1"}, "Potion Speed 1.", 130, 0, 150, 10);
	public static final Value<Integer> potionSpeed2 = new Value<Integer>("Speed2", new String[] {"speed2"}, "Potion Speed 2.", 125, 0, 150, 10);
	public static final Value<Integer> acceleration = new Value<Integer>("Acceleration", new String[] {"accel"}, "Acceleration.", 2149, 1000, 2500, 100);
	public static final Value<Boolean> potion = new Value<Boolean>("Potion", new String[] {"potion"}, "Potion.", false);
	public static final Value<Boolean> step = new Value<Boolean>("SetStep", new String[] {"setstep"}, "bhop->setstep", true);
	
    private int stage;
    private double moveSpeed;
    private double lastDist;
    private int cooldownHops;
	
	public StrafePhobosModule() {
		super("StrafePhobos", new String[] {"strafephobos"}, "Speedhack.", "NONE", -1, ModuleType.MOVEMENT);
        this.stage = 1;
        this.cooldownHops = 0;
	}
	
    @Override
    public void onEnable() {
        this.moveSpeed = getBaseMoveSpeed();
        
        SalHackMod.EVENT_BUS.subscribe(this);
    }
	
    @Override
    public void onDisable() {
        this.moveSpeed = 0.0;
        this.stage = 2;
        
        SalHackMod.EVENT_BUS.unsubscribe(this);
    }
	
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final EventPlayerMotionUpdate event) {
    	if(!this.isEnabled()) return;
    	
    	if(event.getEra() != Era.POST) return;
    	this.lastDist = Math.sqrt((Wrapper.GetMC().player.posX - Wrapper.GetMC().player.prevPosX) * (Wrapper.GetMC().player.posX - Wrapper.GetMC().player.prevPosX) + (Wrapper.GetMC().player.posZ - Wrapper.GetMC().player.prevPosZ) * (Wrapper.GetMC().player.posZ - Wrapper.GetMC().player.prevPosZ));
    }
	
    @SubscribeEvent
    public void onMove(final EventPlayerMove event) {
    	if(!this.isEnabled()) return;
        if (mode.getValue() == Mode.NCP) {
            this.doNCP(event);
        }
        else if (mode.getValue() == Mode.BHOP) {
            float moveForward = Wrapper.GetMC().player.movementInput.moveForward;
            float moveStrafe = Wrapper.GetMC().player.movementInput.moveStrafe;
            float rotationYaw = Wrapper.GetMC().player.rotationYaw;
            if (limiter2.getValue() && Wrapper.GetMC().player.onGround) {
                this.stage = 2;
            }
            if (limiter.getValue() && round(Wrapper.GetMC().player.posY - (int)Wrapper.GetMC().player.posY, 3) == round(0.138, 3)) {
                final EntityPlayerSP player = Wrapper.GetMC().player;
                player.motionY -= 0.13;
                event.Y = (event.Y - 0.13);
                final EntityPlayerSP player2 = Wrapper.GetMC().player;
                player2.posY -= 0.13;
            }
            if (this.stage == 1 && EntityUtil.isMoving()) {
                this.stage = 2;
                this.moveSpeed = this.getMultiplier() * getBaseMoveSpeed() - 0.01;
            }
            else if (this.stage == 2) {
                this.stage = 3;
                if (EntityUtil.isMoving()) {
                    event.Y = (Wrapper.GetMC().player.motionY = 0.4);
                    if (this.cooldownHops > 0) {
                        --this.cooldownHops;
                    }
                    this.moveSpeed *= acceleration.getValue() / 1000.0;
                }
            }
            else if (this.stage == 3) {
                this.stage = 4;
                final double difference = 0.66 * (this.lastDist - getBaseMoveSpeed());
                this.moveSpeed = this.lastDist - difference;
            }
            else {
                if (Wrapper.GetMC().world.getCollisionBoxes((Entity)Wrapper.GetMC().player, Wrapper.GetMC().player.getEntityBoundingBox().offset(0.0, Wrapper.GetMC().player.motionY, 0.0)).size() > 0 || Wrapper.GetMC().player.collidedVertically) {
                    this.stage = 1;
                }
                this.moveSpeed = this.lastDist - this.lastDist / 159.0;
            }
            this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
            if (moveForward == 0.0f && moveStrafe == 0.0f) {
                event.X = (0.0);
                event.Z = (0.0);
                this.moveSpeed = 0.0;
            }
            else if (moveForward != 0.0f) {
                if (moveStrafe >= 1.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? -45.0f : 45.0f);
                    moveStrafe = 0.0f;
                }
                else if (moveStrafe <= -1.0f) {
                    rotationYaw += ((moveForward > 0.0f) ? 45.0f : -45.0f);
                    moveStrafe = 0.0f;
                }
                if (moveForward > 0.0f) {
                    moveForward = 1.0f;
                }
                else if (moveForward < 0.0f) {
                    moveForward = -1.0f;
                }
            }
            final double motionX = Math.cos(Math.toRadians(rotationYaw + 90.0f));
            final double motionZ = Math.sin(Math.toRadians(rotationYaw + 90.0f));
            if (this.cooldownHops == 0) {
                event.X = (moveForward * this.moveSpeed * motionX + moveStrafe * this.moveSpeed * motionZ);
                event.Z = (moveForward * this.moveSpeed * motionZ - moveStrafe * this.moveSpeed * motionX);
            }
            if (step.getValue()) {
                Wrapper.GetMC().player.stepHeight = 0.6f;
            }
            if (moveForward == 0.0f && moveStrafe == 0.0f) {
                event.X = (0.0);
                event.Z = (0.0);
            }
        }
    }
	
    private void doNCP(final EventPlayerMove event) {
    	if(!this.isEnabled()) return;
    	
        if (!limiter.getValue() && Wrapper.GetMC().player.onGround) {
            this.stage = 2;
        }
        switch (this.stage) {
            case 0: {
                ++this.stage;
                this.lastDist = 0.0;
                break;
            }
            case 2: {
                double motionY = 0.40123128;
                if ((Wrapper.GetMC().player.moveForward != 0.0f || Wrapper.GetMC().player.moveStrafing != 0.0f) && Wrapper.GetMC().player.onGround) {
                    if (Wrapper.GetMC().player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        motionY += (Wrapper.GetMC().player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f;
                    }
                    event.Y = (Wrapper.GetMC().player.motionY = motionY);
                    this.moveSpeed *= 2.149;
                    break;
                }
                break;
            }
            case 3: {
                this.moveSpeed = this.lastDist - 0.76 * (this.lastDist - getBaseMoveSpeed());
                break;
            }
            default: {
                if (((limiter2.getValue() && Wrapper.GetMC().world.getCollisionBoxes((Entity)Wrapper.GetMC().player, Wrapper.GetMC().player.getEntityBoundingBox().offset(0.0, Wrapper.GetMC().player.motionY, 0.0)).size() > 0) || Wrapper.GetMC().player.collidedVertically) && this.stage > 0) {
                    this.stage = ((Wrapper.GetMC().player.moveForward != 0.0f || Wrapper.GetMC().player.moveStrafing != 0.0f) ? 1 : 0);
                }
                this.moveSpeed = this.lastDist - this.lastDist / 159.0;
                break;
            }
        }
        this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
        double forward = Wrapper.GetMC().player.movementInput.moveForward;
        double strafe = Wrapper.GetMC().player.movementInput.moveStrafe;
        final double yaw = Wrapper.GetMC().player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.X = (0.0);
            event.Z = (0.0);
        }
        else if (forward != 0.0 && strafe != 0.0) {
            forward *= Math.sin(0.7853981633974483);
            strafe *= Math.cos(0.7853981633974483);
        }
        event.X = ((forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99);
        event.Z = ((forward * this.moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99);
        ++this.stage;
    }
	
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.272;
        if (Wrapper.GetMC().player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Wrapper.GetMC().player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * amplifier;
        }
        return baseSpeed;
    }
	
    private float getMultiplier() {
        float baseSpeed = specialMoveSpeed.getValue();
        if (potion.getValue() && Wrapper.GetMC().player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Wrapper.GetMC().player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier() + 1;
            if (amplifier >= 2) {
                baseSpeed = potionSpeed2.getValue();
            }
            else {
                baseSpeed = potionSpeed.getValue();
            }
        }
        return baseSpeed / 100.0f;
    }
	
    @Override
    public String getMetaData() {
    	if(mode.getValue() == Mode.NONE) return null;
    	return mode.getValue().toString();
    }
    
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        final BigDecimal bigDecimal = new BigDecimal(value).setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
	
	public static enum Mode
    {
        NONE, 
        NCP, 
        BHOP;
    }
}