package com.supersouper.whichery.client.render.entity;

import com.supersouper.whichery.Whichery;
import com.supersouper.whichery.common.entity.demon.EntityPossessedChicken;
import com.supersouper.whichery.common.entity.extendedproperties.DemonologyProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderPossessedChicken extends RenderChicken {

    private static final ResourceLocation possessedChickenTextureEyes = new ResourceLocation(
        Whichery.MODID,
        "textures/models/entity/possessed_chicken_eyes.png");

    public RenderPossessedChicken() {
        super(new ModelChicken(), 0.3f);
        this.setRenderPassModel(this.mainModel);
    }

    protected boolean shouldRenderYellowEyes(EntityPossessedChicken entity, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        if (player == null) {
            return false;
        }

        World world = entity.worldObj;
        DemonologyProperty playerDemonologyProperties = DemonologyProperty.get(player);
        float celestialAngle = world.getCelestialAngle(partialTicks);
        boolean isNight = (celestialAngle > 0.25F && celestialAngle < 0.75F);

        boolean isNightAndLightningBoltDitHitRecently = isNight && world.lastLightningBolt > 0;
        boolean canPlayerSeeDemons = playerDemonologyProperties != null
            && playerDemonologyProperties.isCanSeeDemonsPossessingHosts();

        return isNightAndLightningBoltDitHitRecently || canPlayerSeeDemons;
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTicks) {
        EntityPossessedChicken chicken = (EntityPossessedChicken) entity;

        if (pass == 1) {
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(0, 0);
        }

        if (pass != 0 || !shouldRenderYellowEyes(chicken, partialTicks)) {
            return -1;
        } else {
            this.bindTexture(possessedChickenTextureEyes);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            GL11.glDepthMask(!entity.isInvisible());

            // prevent z-fighting
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(-3, -3);

            adjustBrightnessToSurroundings(chicken, partialTicks);

            return 1;
        }
    }

    @Override
    protected int inheritRenderPass(EntityLivingBase p_77035_1_, int p_77035_2_, float p_77035_3_) {
        /*
         * The super implementation calls shouldRenderPass() again, but we need no logic here for render passes.
         * Otherwise, the hurt animation for this layer (which we don't need anyway) would use the same brightness calculation as the eyes,
         * and thus render the chicken with higher brightness when hurt/dying.
         */
        return -1;
    }

    /*
     * Make the eyes glow; don't use a fixed brightness as that does not look good in all circumstances (moody vs
     * full-bright, fog, thunder, rain, daylight),
     * as the default chicken texture is white, and the eye is only one pixel, which limits our abilities to add
     * contrast.
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
        GL11.glColor4f(1, 1, 1, 1);
    }
}
