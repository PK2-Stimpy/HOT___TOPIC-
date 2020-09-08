package me.ionar.salhack.gui.hud.components;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class InventoryComponent extends HudComponentItem
{
	public final Value<Boolean> ShowHotbar = new Value<Boolean>("ShowHotbar", new String[] {""}, "Displays the hotbar", false);
    public final Value<Boolean> ShowXCarry = new Value<Boolean>("ShowXCarry", new String[] {""}, "Displays the crafting inventory", true);
    public final Value<Boolean> Background = new Value<Boolean>("Background", new String[] {""}, "Displays the Background", true);
    public final Value<Float> Scale = new Value<Float>("Scale", new String[] {""}, "Allows you to modify the scale", 1.0f, 0.0f, 10.0f, 1.0f);
	
    public InventoryComponent()
    {
        super("Inventory", 2, 15);
        SetHidden(false);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks)
    {
        super.render(p_MouseX, p_MouseY, p_PartialTicks);


        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.scale(Scale.getValue(), Scale.getValue(), Scale.getValue());
        if (Background.getValue())
            RenderUtil.drawRect(GetX(), GetY(), GetX() + GetWidth(), GetY() + GetHeight(), 0x75101010); // background
        for (int i = 0; i < 27; i++)
        {
            ItemStack itemStack = mc.player.inventory.mainInventory.get(i + 9);
            int offsetX = (int) GetX() + (i % 9) * 16;
            int offsetY = (int) GetY() + (i / 9) * 16;
			mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
		}

		if (ShowHotbar.getValue())
        {
	        for (int i = 0; i < 9; i++)
	        {
	            ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
	            int offsetX = (int) GetX() + (i % 9) * 16;
	            int offsetY = (int) GetY() + 48;
	            mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
	            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
	        }
        }

		if (ShowXCarry.getValue())
		{
	        if (Background.getValue())
	            RenderUtil.drawRect(GetX() + GetWidth(), GetY(), GetX() + GetWidth() + 32, GetY() + 32, 0x75101010); // background
    
            for (int i = 1; i < 5; i++)
            {
                ItemStack itemStack = mc.player.inventoryContainer.getInventory().get(i);
    
                int offsetX = (int) GetX();
                int offsetY = (int) GetY();
    
                switch (i)
                {
                    case 1:
                    case 2:
                        offsetX += 128 + (i * 16);
                        break;
                    case 3:
                    case 4:
                        offsetX += 128 + ((i - 2) * 16);
                        offsetY += 16;
                        break;
                }
    
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
            }
		}
		
        SetWidth(16 * 9 );
        SetHeight(16 * (ShowHotbar.getValue() ? 4 : 3));

        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.popMatrix();
    }
}
