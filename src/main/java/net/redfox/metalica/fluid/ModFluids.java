/* (C)2025 */
package net.redfox.metalica.fluid;

import static net.redfox.metalica.fluid.BurningLiquidBlock.createBurning;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.redfox.metalica.Metalica;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FlowingFluidObject;

public class ModFluids {
  public static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(Metalica.MOD_ID);

  public static FlowingFluidObject<ForgeFlowingFluid> createMoltenFluid(String name) {
    return FLUIDS
        .register(name)
        .type(hot(name).temperature(1100).lightLevel(12))
        .block(createBurning(MapColor.RAW_IRON, 12, 10, 5f))
        .bucket()
        .commonTag()
        .flowing();
  }

  private static FluidType.Properties hot(String name) {
    return FluidType.Properties.create()
        .density(2000)
        .viscosity(10000)
        .temperature(1000)
        .descriptionId("fluid." + Metalica.MOD_ID + "." + name)
        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
        // from forge lava type
        .motionScale(0.0023333333333333335D)
        .canSwim(false)
        .canDrown(false)
        .pathType(BlockPathTypes.LAVA)
        .adjacentPathType(null);
  }

  public static void register(IEventBus eventBus) {
    FLUIDS.register(eventBus);
  }
}
