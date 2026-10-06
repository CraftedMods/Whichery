package com.supersouper.whichery.api.demonology;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.function.Function;

@Desugar
public record PossessedAnimalRegistration<A extends EntityLivingBase, P extends Entity & IPossessedAnimal>(
    Class<A> animal,
    Class<P> possessedAnimal,
    Function<A, P> createPossessedEntityFunction) {
}
