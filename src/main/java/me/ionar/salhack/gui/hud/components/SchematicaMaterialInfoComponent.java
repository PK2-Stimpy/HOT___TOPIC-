package me.ionar.salhack.gui.hud.components;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.github.lunatrius.core.client.gui.GuiHelper;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.util.BlockList;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.util.ItemStackSortType;
import com.mojang.realmsclient.gui.ChatFormatting;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.util.render.RenderUtil;
import net.minecraft.client.Minecraft;

public class SchematicaMaterialInfoComponent extends HudComponentItem
{
    public SchematicaMaterialInfoComponent()
    {
        super("SchematicaMaterialInfo", 300, 300);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks)
    {
        super.render(p_MouseX, p_MouseY, p_PartialTicks);
        
        GL11.glPushMatrix();
        
        RenderUtil.drawRect(GetX(), GetY(), GetX()+GetWidth(), GetY()+GetHeight(), 0x75101010);
        
        if (SchematicPrinter.INSTANCE.getSchematic() == null)
        {
            final String l_String = "No Schematic loaded";
            
            RenderUtil.drawStringWithShadow(l_String, GetX(), GetY(), -1);
            SetWidth(RenderUtil.getStringWidth(l_String));
            SetHeight(RenderUtil.getStringHeight(l_String));
            GL11.glPopMatrix();
            return;
        }

        final List<BlockList.WrappedItemStack> blockList = new BlockList().getList(mc.player, SchematicPrinter.INSTANCE.getSchematic(), mc.world);
        
        ItemStackSortType.fromString("SIZE_DESC").sort(blockList);
        
        float l_Height = 0;
        float l_MaxWidth = 0;
        
        for (BlockList.WrappedItemStack l_Stack : blockList)
        {
            String l_String = String.format("%s: %s", l_Stack.getItemStackDisplayName(), l_Stack.getFormattedAmount(), l_Stack.getFormattedAmount());

            GuiHelper.drawItemStack(l_Stack.itemStack, (int)GetX(), (int)(GetY()+l_Height));
            float l_Width = RenderUtil.drawStringWithShadow(l_String, GetX()+20, GetY()+l_Height+4, -1)+22;
            
            if (l_Width >= l_MaxWidth)
                l_MaxWidth = l_Width;
            
            l_Height += 16;
        }
        
        SetWidth(l_MaxWidth);
        SetHeight(l_Height);
        
        GL11.glPopMatrix();
    }

}
