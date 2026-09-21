package com.supersouper.whichery.client.render.entity;

import com.supersouper.whichery.Whichery;
import com.supersouper.whichery.common.entity.demon.EntityPossessedChicken;
import com.supersouper.whichery.common.entity.extendedproperties.DemonologyProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderPossessedChicken extends RenderChicken {

    private static final ResourceLocation possessedChickenTextures = new ResourceLocation(Whichery.MODID, "textures/models/entity/possessed_chicken.png");

    public RenderPossessedChicken() {
        super(new ModelChicken(), 0.3f);
        this.setRenderPassModel(new ModelChicken());
    }

    protected boolean shouldRenderYellowEyes(EntityPossessedChicken entity, float partialTicks) {
        var world = entity.worldObj;
        var playerDemonologyProperties = DemonologyProperty.get(Minecraft.getMinecraft().thePlayer);
        var celestialAngle = entity.worldObj.getCelestialAngle(partialTicks);
        var isNight = (celestialAngle > 0.25F && celestialAngle < 0.75F);

        boolean isNightAndLightningBoltDitHitRecently = isNight && world.lastLightningBolt > 0;
        boolean canPlayerSeeDemons = playerDemonologyProperties != null && playerDemonologyProperties.isCanSeeGlowingEyesOfDemonHosts();

        return isNightAndLightningBoltDitHitRecently || canPlayerSeeDemons;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        var chicken = (EntityPossessedChicken) entity;

        if (pass != 0 || !shouldRenderYellowEyes(chicken, partialTicks)) {
            return -1;
        } else {
            this.bindTexture(possessedChickenTextures);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            GL11.glDepthMask(!entity.isInvisible());

            int blockLight = 240;
            int skyLight = 240;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) blockLight, (float) skyLight);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            float scale = 1.003F;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0.0F, -0.002F, 0.0F);

            return 1;
        }
    }
}
