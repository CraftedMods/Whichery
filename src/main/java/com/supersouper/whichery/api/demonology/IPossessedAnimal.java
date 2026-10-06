package com.supersouper.whichery.api.demonology;

import net.minecraft.entity.EntityLivingBase;

import com.supersouper.whichery.common.entity.demon.EntityDemonicShadow;

public interface IPossessedAnimal extends IPossessedEntity {

    static boolean canBePossessed(EntityLivingBase entityLivingBase) {
        if (entityLivingBase != null) {
            return entityLivingBase.isEntityAlive() && !(entityLivingBase instanceof IDemon)
                && !(entityLivingBase.riddenByEntity instanceof EntityDemonicShadow)
                && PossessedAnimalRegistry.isPossessedVariantRegisteredForHost(entityLivingBase);
        }

        return false;
    }

}
