package com.supersouper.whichery.common.items;

import com.supersouper.whichery.common.entity.extendedproperties.DemonologyProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemAppleThatMakesYouSeeDemons extends ItemFood {

    public ItemAppleThatMakesYouSeeDemons() {
        super(0, false);
        setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            DemonologyProperty props = DemonologyProperty.get(player);
            props.setCanSeeDemonsPossessingHosts(!props.isCanSeeDemonsPossessingHosts(), player);
        }

        super.onFoodEaten(stack, world, player);
    }
}
