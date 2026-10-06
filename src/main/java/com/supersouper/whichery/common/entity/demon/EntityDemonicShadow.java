package com.supersouper.whichery.common.entity.demon;

import com.supersouper.whichery.api.demonology.IDemon;
import com.supersouper.whichery.common.entity.ai.EntityAIDemonicShadowIdle;
import com.supersouper.whichery.common.entity.ai.EntityAIFlyToTargetAndAttach;
import com.supersouper.whichery.common.entity.ai.EntityAIPossessTarget;
import com.supersouper.whichery.common.entity.ai.EntityAIRandomTargetToPossess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.world.World;

public class EntityDemonicShadow extends EntityFlying implements IDemon {

    public EntityDemonicShadow(World world) {
        super(world);

        this.setSize(1, 1.5f);
        this.noClip = true;

        setupAI();
    }

    private void setupAI() {
        tasks.addTask(0, new EntityAIPossessTarget(this));
        tasks.addTask(1, new EntityAIFlyToTargetAndAttach(this, 0.3));
        tasks.addTask(2, new EntityAIDemonicShadowIdle(this));

        targetTasks.addTask(0, new EntityAIRandomTargetToPossess(this, 16));
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }

    private static final int SHOULD_SHRINK_WATCHER_ID = 25;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(SHOULD_SHRINK_WATCHER_ID, 0);
    }

    public void setShouldShrink(boolean shouldShrink) {
        this.dataWatcher.updateObject(SHOULD_SHRINK_WATCHER_ID, shouldShrink ? 1 : 0);
    }

    public boolean getShouldShrink() {
        return this.dataWatcher.getWatchableObjectInt(SHOULD_SHRINK_WATCHER_ID) == 1;
    }

    private int shrinkTicksClient = -1;

    // Getter für den Renderer auf dem Client (Gibt einen Faktor von 0.0 bis 1.0 zurück)
    public float getShrinkFactor() {
        if (this.isDead || (getShouldShrink() && this.ridingEntity == null)) {
            return 1.0F;
        }

        if (getShouldShrink()) {

            if (shrinkTicksClient < 20) {
                return 0;
            }

            // 20 Ticks = 1 Sekunde Linger-Dauer
            float factor = (float) (shrinkTicksClient - 20) / 60;
            if (factor > 1) factor = 1;
            return factor;
        }

        return 0;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public double getYOffset() {
        if (this.ridingEntity != null || getShouldShrink()) {
            return (this.ridingEntity.height / 2) - this.height / 2;
        }
        return super.getYOffset();
    }

    public void hiss() {
        float spawnPitch = 0.4F + this.rand.nextFloat() * 0.1F;

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.wither.shoot", 0.75f, spawnPitch);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (getShouldShrink()) {
            ++shrinkTicksClient;
        } else {
            shrinkTicksClient = 0;
        }

        this.isAirBorne = false;

        if (!this.worldObj.isRemote && !this.hasPlayedSpawnSound) { // todo persist
            hiss();

            this.hasPlayedSpawnSound = true;
        }

        if (worldObj.isRemote) {
            double speedX = this.motionX;
            double speedY = this.motionY;
            double speedZ = this.motionZ;
            double speed = Math.sqrt(speedX * speedX + speedY * speedY + speedZ * speedZ);

            int particleCount = speed > 0.05 ? 6 : 3;

            for (int i = 0; i < particleCount; ++i) {
                double px = this.posX + (this.rand.nextDouble() - 0.5) * 0.4;
                double py = this.posY + this.rand.nextDouble() * this.height;
                double pz = this.posZ + (this.rand.nextDouble() - 0.5) * 0.4;

                double red = 0.001D;
                double green = 0.001D;
                double blue = 0.001D;

                this.worldObj.spawnParticle("mobSpellAmbient", px, py, pz, red, green, blue);
            }
        }
    }

    private boolean hasPlayedSpawnSound = false;

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    protected void collideWithEntity(Entity entity) {
        // Do nothing
    }
}
