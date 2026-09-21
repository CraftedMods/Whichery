package com.supersouper.whichery.common.entity.extendedproperties;

import com.supersouper.whichery.Whichery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class DemonologyProperty implements IExtendedEntityProperties {

    public static final String KEY = Whichery.MODID + "DemonologyProperty";

    private boolean canSeeGlowingEyesOfDemonHosts;

    public boolean isCanSeeGlowingEyesOfDemonHosts() {
        return canSeeGlowingEyesOfDemonHosts;
    }

    public void setCanSeeGlowingEyesOfDemonHosts(boolean canSeeGlowingEyesOfDemonHosts) {
        this.canSeeGlowingEyesOfDemonHosts = canSeeGlowingEyesOfDemonHosts;
    }

    public static DemonologyProperty get(EntityPlayer player) {
        return (DemonologyProperty) player.getExtendedProperties(KEY);
    }

    public static void syncToClient(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            DemonologyProperty props = get(player);
            if (props != null) {
                // todo PacketHandler.INSTANCE.sendTo(new VampireStatsPacket(props), (EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        var tag = new NBTTagCompound();

        tag.setBoolean("canSeeGlowingEyesOfDemonHosts", canSeeGlowingEyesOfDemonHosts);

        compound.setTag(KEY, tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        var tag = compound.getCompoundTag(KEY);

        if (tag != null) {
            canSeeGlowingEyesOfDemonHosts = tag.getBoolean("canSeeGlowingEyesOfDemonHosts");
        }
    }

    @Override
    public void init(Entity entity, World world) {

    }
}
