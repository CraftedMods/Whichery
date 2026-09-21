package com.supersouper.whichery.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.Map;
import java.util.Objects;

/**
 * AI profiles are groups of tasks that are added/removed together. For example, an entity could have two entirely and
 * mutually exclusive behavior modes, which each contain multiple AI tasks. Instead of implementing this behavior via mutex bits
 * and the manual addition/removal of tasks, the idea of profiles greatly simplifies the handling.
 */
public class EntityAIProfileManager {

    private final EntityLiving entityLiving;

    private EntityAIProfile currentProfile;

    public EntityAIProfileManager(EntityLiving entityLiving) {
        this.entityLiving = Objects.requireNonNull(entityLiving);
    }

    public void setProfile(EntityAIProfile newProfile) {
        if (currentProfile != null) {
            removeProfile(currentProfile);
        }

        this.currentProfile = newProfile;

        if (newProfile != null) {
            applyProfile(newProfile);
        }
    }

    private void applyProfile(EntityAIProfile profile) {
        applyTaskList(entityLiving.tasks, profile.tasksWithPriority());
        applyTaskList(entityLiving.targetTasks, profile.targetTasksWithPriority());
    }

    private void applyTaskList(EntityAITasks entityTasks, Map<EntityAIBase, Integer> taskList) {
        taskList.forEach((task, priority) -> {
            entityTasks.addTask(priority, task);
        });
    }

    private void removeProfile(EntityAIProfile profile) {
        removeTaskList(entityLiving.tasks, profile.tasksWithPriority());
        removeTaskList(entityLiving.targetTasks, profile.targetTasksWithPriority());
    }

    private void removeTaskList(EntityAITasks entityTasks, Map<EntityAIBase, Integer> taskList) {
        taskList.forEach((task, priority) -> {
            entityTasks.removeTask(task);
        });
    }

}
