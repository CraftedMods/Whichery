package com.supersouper.whichery.common.entity.extendedproperties;

import com.supersouper.whichery.Whichery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class DemonologyProperty implements IExtendedEntityProperties {

    public static final String KEY = Whichery.MODID + "DemonologyProperty";

    private boolean canSeeDemonsPossessingHosts;

    public boolean isCanSeeDemonsPossessingHosts() {
        return canSeeDemonsPossessingHosts;
    }

    public void setCanSeeDemonsPossessingHosts(boolean canSeeDemonsPossessingHosts) {
        this.canSeeDemonsPossessingHosts = canSeeDemonsPossessingHosts;
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
        NBTTagCompound tag = new NBTTagCompound();

        tag.setBoolean("CanSeeDemonsPossessingHosts", canSeeDemonsPossessingHosts);

        compound.setTag(KEY, tag);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag(KEY);

        if (tag != null) {
            canSeeDemonsPossessingHosts = tag.getBoolean("CanSeeDemonsPossessingHosts");
        }
    }

    @Override
    public void init(Entity entity, World world) {

    }
}
