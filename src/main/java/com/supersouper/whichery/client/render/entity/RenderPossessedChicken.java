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
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderPossessedChicken extends RenderChicken {

    private static final ResourceLocation possessedChickenTextures = new ResourceLocation(Whichery.MODID, "textures/models/entity/possessed_chicken.png");

    public RenderPossessedChicken() {
        super(new ModelChicken(), 0.3f);
        this.setRenderPassModel(new ModelChicken());
    }

    protected boolean shouldRenderYellowEyes(EntityPossessedChicken entity, float partialTicks) {
        World world = entity.worldObj;
        DemonologyProperty playerDemonologyProperties = DemonologyProperty.get(Minecraft.getMinecraft().thePlayer);
        float celestialAngle = world.getCelestialAngle(partialTicks);
        boolean isNight = (celestialAngle > 0.25F && celestialAngle < 0.75F);

        boolean isNightAndLightningBoltDitHitRecently = isNight && world.lastLightningBolt > 0;
        boolean canPlayerSeeDemons = playerDemonologyProperties != null && playerDemonologyProperties.isCanSeeDemonsPossessingHosts();

        return isNightAndLightningBoltDitHitRecently || canPlayerSeeDemons;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        EntityPossessedChicken chicken = (EntityPossessedChicken) entity;

        if (pass != 0 || !shouldRenderYellowEyes(chicken, partialTicks)) {
            return -1;
        } else {
            this.bindTexture(possessedChickenTextures);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            GL11.glDepthMask(!entity.isInvisible());

            adjustBrightnessToSurroundings(chicken, partialTicks);

            // Scale the eye layer a bit so we don't have z-fighting
            float scale = 1.003F; // todo is scale necessary or only translate?
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0.0F, -0.002F, 0.0F);

            return 1;
        }
    }

    /*
     * Make the eyes glow; don't use a fixed brightness as that does not look good in all circumstances (moody vs full-bright, fog, thunder, rain, daylight),
     * as the default chicken texture is white, and the eye is only one pixel, which limits our abilities to add contrast.
     */
    private void adjustBrightnessToSurroundings(EntityPossessedChicken chicken, float partialTicks) {
        int currentMobBrightness = chicken.getBrightnessForRender(partialTicks);
        int blockLight = currentMobBrightness % 65536;
        int skyLight = currentMobBrightness / 65536;

        // Make it brighter than the surroundings so it glows nicely in contrast to them, but not too bright
        blockLight = Math.min(240, blockLight + 60);
        skyLight = Math.min(240, skyLight + 60);

        // At low light levels require at least a minimum brightness, as the glow should be very visible then
        if (blockLight < 195) blockLight = 195;
        if (skyLight < 195) skyLight = 195;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) blockLight, (float) skyLight);
    }
}
