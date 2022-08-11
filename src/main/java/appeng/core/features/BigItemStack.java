package appeng.core.features;

import appeng.core.AELog;
import gregtech.api.util.GTLog;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BigItemStack {

    public static final BigItemStack EMPTY = new BigItemStack(ItemStack.EMPTY);
    private ItemStack item;
    private int stackSize;
    private final int maxStackSize = 2048;

    public BigItemStack (ItemStack item, int amount){
        this.item = item;
        this.item.setCount(1);
        this.stackSize = amount;
        AELog.info("Created BigItemStack: " + item + " with count: " + stackSize);
    }

    public BigItemStack(ItemStack item){
        this(item, item.getTagCompound().getInteger("BigCount"));
    }

    public ItemStack[] convertToStacks(){
        List<ItemStack> itemStackList = new ArrayList<>();
        int remainder = stackSize % item.getMaxStackSize();
        ItemStack modifiableStack = item.copy();


        if (stackSize > item.getMaxStackSize()) {
            ItemStack split;
            // item.setCount(item.getMaxStackSize());

            while (modifiableStack.getCount() >= modifiableStack.getMaxStackSize()) {
                split = modifiableStack.copy();
                split.setCount(modifiableStack.getMaxStackSize());
                modifiableStack.setCount(modifiableStack.getCount() - modifiableStack.getMaxStackSize());
                itemStackList.add(split);
            }
        }
        if (remainder > 0) {
            modifiableStack.setCount(remainder);
            itemStackList.add(modifiableStack.copy());
        }
        return itemStackList.toArray(new ItemStack[0]);
    }
    public ItemStack getItemStack(){
        item.getTagCompound().setInteger("BigCount", stackSize);
        return item;
    }
    public int getTotalItemStacks(){
        return (int) Math.ceil((double) stackSize / item.getMaxStackSize());
    }

    public int getMaxStackSize(){
        return maxStackSize;
    }
    public int getCount(){
        return stackSize;
    }
    public void setCount(int amount){
        this.stackSize = amount;
    }
    public boolean isEmpty(){
        if (stackSize > 0)
            return false;

        return item.isEmpty();
    }

    public void writeToNBT(NBTTagCompound tag) {
        item.writeToNBT(tag);
        tag.setInteger("BigCount", stackSize);
    }
}
