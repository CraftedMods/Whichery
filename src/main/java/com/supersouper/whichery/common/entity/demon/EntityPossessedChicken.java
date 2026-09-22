package com.supersouper.whichery.common.entity.demon;

import com.google.common.collect.ImmutableMap;
import com.supersouper.whichery.common.entity.ai.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.Map;

import static com.supersouper.whichery.common.util.TimeUtils.minutesToTicks;
import static java.util.Collections.emptyMap;

public class EntityPossessedChicken extends EntityChicken implements IPossessedEntity {

    private static final String POSSESSED_ANIMAL_BEHAVIOR_NBT_KEY = "PossessedAnimalBehavior";
    private static final String POSSESSED_ANIMAL_BEHAVIOR_DURATION_TICKS_NBT_KEY = "PossessedAnimalBehaviorDurationTicks";

    protected final EntityAIProfileManager entityAIProfileManager = new EntityAIProfileManager(this);

    private final EntityAIProfile MIMIC_NORMAL_CHICKEN_AI_PROFILE = new EntityAIProfile(
        ImmutableMap.of(
            new EntityAIPanic(this, 1.4D), 1,
            new EntityAIMate(this, 1.0D), 2,
            new EntityAITempt(this, 1.0D, Items.wheat_seeds, false), 3,
            new EntityAIFollowParent(this, 1.1D), 4
        ),
        emptyMap()
    );

    private final EntityAIProfile BERSERK_AI_PROFILE = new EntityAIProfile(
        ImmutableMap.of(
            new EntityAIAttackOnCollide(this, EntityChicken.class, 1.0D, false), 3,
            new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false), 4,
            new EntityAIMoveTowardsTarget(this, 1.8, 16), 5
        ),
        ImmutableMap.of(
            new EntityAINearestAttackableTarget(this, EntityChicken.class, 10, false, false, entity -> entity instanceof EntityChicken && !(entity instanceof EntityPossessedChicken)), 4,
            new EntityAINearestAttackableTarget(this, EntityPlayer.class, 5, false), 5
        )
    );

    private final EntityAIProfile WEIRD_AI_PROFILE = new EntityAIProfile(
        ImmutableMap.of(
            new EntityAICreepyStare(this, EntityPlayer.class, 16, 0.5f, minutesToTicks(2), true), 4,
            new EntityAIStareWeird(this, 0.5f, minutesToTicks(4)), 5,
            new EntityAIJumpAndScream(this, 1f, getHurtSound()), 6
        ),
        emptyMap()
    );

    private final Map<EnumPossessedAnimalBehavior, EntityAIProfile> AI_PROFILES_BY_BEHAVIOR = ImmutableMap.of(
        EnumPossessedAnimalBehavior.MIMIC_NORMAL, MIMIC_NORMAL_CHICKEN_AI_PROFILE,
        EnumPossessedAnimalBehavior.BERSERK, BERSERK_AI_PROFILE,
        EnumPossessedAnimalBehavior.WEIRD, WEIRD_AI_PROFILE
    );

    private EnumPossessedAnimalBehavior possessedAnimalBehavior = EnumPossessedAnimalBehavior.MIMIC_NORMAL;
    private int possessedAnimalBehaviorDurationTicks = minutesToTicks(60);

    public EntityPossessedChicken(World world) {
        super(world);

        setupAi();
    }

    protected void setupAi() {
        this.tasks.taskEntries.clear();

        // Default AI tasks, regardless of behavior
        this.tasks.addTask(0, new EntityAISwimming(this));

        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(9, new EntityAILookIdle(this));

        // Applies the AI profile for the default behavior
        setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.MIMIC_NORMAL, minutesToTicks(60));
    }

    public boolean attackEntityAsMob(Entity entityToAttack) {
        return entityToAttack.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setString(POSSESSED_ANIMAL_BEHAVIOR_NBT_KEY, possessedAnimalBehavior.getNbtName());
        tagCompound.setInteger(POSSESSED_ANIMAL_BEHAVIOR_DURATION_TICKS_NBT_KEY, possessedAnimalBehaviorDurationTicks);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        EnumPossessedAnimalBehavior deserializedBehavior = EnumPossessedAnimalBehavior.deserializeFromNbt(tagCompound.getString(POSSESSED_ANIMAL_BEHAVIOR_NBT_KEY));
        int possessedAnimalBehaviorDurationTicks = tagCompound.getInteger(POSSESSED_ANIMAL_BEHAVIOR_DURATION_TICKS_NBT_KEY);

        setPossessedAnimalBehavior(deserializedBehavior, possessedAnimalBehaviorDurationTicks);
    }

    public void setPossessedAnimalBehavior(EnumPossessedAnimalBehavior newBehavior, int durationTicks) {
        possessedAnimalBehaviorDurationTicks = durationTicks;

        if (this.possessedAnimalBehavior == newBehavior) {
            return;
        }

        if (newBehavior != null) {
            possessedAnimalBehavior = newBehavior;
        } else {
            possessedAnimalBehavior = EnumPossessedAnimalBehavior.MIMIC_NORMAL;
        }

        entityAIProfileManager.setProfile(AI_PROFILES_BY_BEHAVIOR.get(this.possessedAnimalBehavior));
    }

    @Override
    protected void updateAITick() {
        super.updateAITick();

        if (possessedAnimalBehaviorDurationTicks <= 0) {
            decideNewPossessedAnimalBehavior();
        } else {
            --possessedAnimalBehaviorDurationTicks;
        }
    }

    private void decideNewPossessedAnimalBehavior() {
        switch (possessedAnimalBehavior) {
            case BERSERK:
                setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.MIMIC_NORMAL, minutesToTicks(60));
                break;
            case MIMIC_NORMAL:
                int diceRollAfterMimicNormal = getRNG().nextInt(15);

                if (diceRollAfterMimicNormal == 0) {
                    setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.BERSERK, minutesToTicks(0.75));
                } else if (diceRollAfterMimicNormal < 5) {
                    setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.WEIRD, minutesToTicks(5));
                } else {
                    setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.MIMIC_NORMAL, minutesToTicks(10));
                }
                break;
            case WEIRD:
                int diceRollAfterWeird = getRNG().nextInt(10);

                if (diceRollAfterWeird == 0) {
                    setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.BERSERK, minutesToTicks(0.5));
                } else if (diceRollAfterWeird < 3) {
                    setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.WEIRD, minutesToTicks(3));
                } else {
                    setPossessedAnimalBehavior(EnumPossessedAnimalBehavior.MIMIC_NORMAL, minutesToTicks(30));
                }
                break;
        }
    }

    @Override
    public EntityLiving createUnpossessedDummyHostEntity() {
        EntityChicken chicken = new EntityChicken(worldObj);
        chicken.setHealth(this.getHealth());
        chicken.setGrowingAge(this.getGrowingAge());

        return chicken;
    }

    public static EntityPossessedChicken createPossessedChicken(EntityChicken chicken) {
        EntityPossessedChicken possessedChicken = new EntityPossessedChicken(chicken.worldObj);
        possessedChicken.setLocationAndAngles(
            chicken.posX,
            chicken.posY,
            chicken.posZ,
            chicken.rotationYaw,
            chicken.rotationPitch
        );

        possessedChicken.setHealth(chicken.getHealth());
        possessedChicken.setGrowingAge(chicken.getGrowingAge());

        return possessedChicken;
    }
}
