package com.supersouper.whichery.common.entity.demon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPossessedChicken extends EntityChicken {

    public EntityPossessedChicken(World p_i1682_1_) {
        super(p_i1682_1_);
        setupAi();
    }

    protected void setupAi() {
        // todo currently attacks other possessed chickens
        this.tasks.taskEntries.clear();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityChicken.class, 1.0D, false));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new EntityAIMoveTowardsTarget(this, 1, 16));
        this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.wheat_seeds, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));

        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityChicken.class, 200, false));
    }

    public boolean attackEntityAsMob(Entity p_70652_1_)
    {
        return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }
}
