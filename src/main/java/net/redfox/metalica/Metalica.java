/* (C)2025 */
package net.redfox.metalica;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.redfox.metalica.block.ModBlocks;
import net.redfox.metalica.fluid.ModFluids;
import net.redfox.metalica.item.ModCreativeTabs;
import net.redfox.metalica.item.ModItems;
import net.redfox.metalica.material.Metals;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Metalica.MOD_ID)
public class Metalica {
  // Define mod id in a common place for everything to reference
  public static final String MOD_ID = "metalica";
  // Directly reference a slf4j logger
  public static final Logger LOGGER = LogUtils.getLogger();

  static {
    Metals.createMaterials();
  }

  public Metalica(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    ModCreativeTabs.register(modEventBus);

    ModItems.register(modEventBus);
    ModBlocks.register(modEventBus);
    if (ModList.get().isLoaded("mantle")) {
      ModFluids.register(modEventBus);
    }

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
  }
}
