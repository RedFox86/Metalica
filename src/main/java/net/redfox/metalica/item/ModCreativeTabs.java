package net.redfox.metalica.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.material.MetalMaterial;

public class ModCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Metalica.MOD_ID);

  public static final RegistryObject<CreativeModeTab> METALICA_TAB = CREATIVE_MODE_TABS.register("metalica_tab", () -> CreativeModeTab.builder()
      .icon(() -> new ItemStack(Items.IRON_INGOT))
      .title(Component.translatable("creativetab.metalica.metalica_tab"))
      .displayItems((pParameters, pOutput) -> {
        for (MetalMaterial material : MetalMaterial.getMaterials()) {
          pOutput.accept(material.getIngot().get());
          pOutput.accept(material.getNugget().get());
          pOutput.accept(material.getDust().get());
          pOutput.accept(material.getStorageBlock().get());
          if (ModList.get().isLoaded("mantle")) pOutput.accept(material.getFluid().getBucket());
          if (!material.hasOre()) continue;
          pOutput.accept(material.getRawStorageBlock().get());
          pOutput.accept(material.getRaw().get());
          pOutput.accept(material.getStoneOre().get());
          pOutput.accept(material.getDeepslateOre().get());
        }
      })
      .build());


  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}