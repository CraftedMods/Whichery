package com.supersouper.whichery.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;

import java.util.Map;

/**
 * A group of AI tasks that can be added/removed together.
 */
public class EntityAIProfile {

    private final Map<EntityAIBase, Integer> tasksWithPriority;
    private final Map<EntityAIBase, Integer> targetTasksWithPriority;

    public EntityAIProfile(Map<EntityAIBase, Integer> tasksWithPriority, Map<EntityAIBase, Integer> targetTasksWithPriority) {
        this.tasksWithPriority = tasksWithPriority;
        this.targetTasksWithPriority = targetTasksWithPriority;
    }

    public Map<EntityAIBase, Integer> tasksWithPriority() {
        return tasksWithPriority;
    }

    public Map<EntityAIBase, Integer> targetTasksWithPriority() {
        return targetTasksWithPriority;
    }

}
