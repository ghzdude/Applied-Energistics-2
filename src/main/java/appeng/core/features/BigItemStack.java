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

    private final int maxStackSize = 2048;

    public static final BigItemStack EMPTY = new BigItemStack(ItemStack.EMPTY);
    private ItemStack item;
    private int stackSize;

    public BigItemStack (ItemStack item, int amount){
        this.item = item;
        writeStackSizeNBT(amount);
        // this.item.setCount(1);
    }

    public BigItemStack(ItemStack item){
        this.item = item;
        writeStackSizeNBT(item.getCount());
        // this.item.setCount(1);
    }

    public ItemStack[] convertToStacks(){
        // List<ItemStack> itemStackList = new ArrayList<>();
        ItemStack[] itemStacks = new ItemStack[getTotalItemStacks()];
        ItemStack itemStack = this.item.copy();

        int stackSize = itemStack.getTagCompound().getInteger("BigCount");
        itemStack.setCount(itemStack.getMaxStackSize());

        for (int i = 0; i < itemStacks.length; i++) {
            if (stackSize > itemStack.getMaxStackSize()){
                itemStacks[i] = itemStack;
                stackSize -= itemStack.getMaxStackSize();
            } else {
                itemStack.setCount(stackSize);
                itemStacks[i] = itemStack;
            }
        }
        return itemStacks;
        /*
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
         */
    }

    public ItemStack getItemStack(){
        return item;
    }

    public int getTotalItemStacks(){
        return (int) Math.ceil((double) stackSize / item.getMaxStackSize());
    }

    public int getMaxStackSize(){
        return maxStackSize;
    }

    public int getCount(){
        readStackSizeNBT();
        return stackSize;
    }

    public void setCount(int amount){
        // this.stackSize = amount;
        writeStackSizeNBT(amount);
    }
    public boolean isEmpty(){
        readStackSizeNBT();
        if (stackSize > 0)
            return false;

        return item.isEmpty();
    }

    public void writeToNBT(NBTTagCompound tag) {
        item.writeToNBT(tag);
    }

    private void writeStackSizeNBT(int amount){
        if (!item.hasTagCompound())
            item.setTagCompound(new NBTTagCompound());

        item.getTagCompound().setInteger("BigCount", amount);
    }

    private void writeStackSizeNBT(){
        writeStackSizeNBT(this.stackSize);
    }

    private void readStackSizeNBT (){
        if (this.item.getTagCompound() == null){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("BigCount", this.item.getCount());
        }

        this.stackSize = item.getTagCompound().getInteger("BigCount");
    }
}
