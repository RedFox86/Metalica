package net.redfox.metalica.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.block.ModBlocks;

public class ModCreativeTabs {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Metalica.MOD_ID);

  public static final RegistryObject<CreativeModeTab> METALICA_TAB = CREATIVE_MODE_TABS.register("metalica_tab", () -> CreativeModeTab.builder()
      .icon(() -> new ItemStack(ModItems.ALUMINUM_INGOT.get()))
      .title(Component.translatable("creativetab.metalica.metalica_tab"))
      .displayItems((pParameters, pOutput) -> {
        pOutput.accept(ModBlocks.ALUMINUM_BLOCK.get());
        pOutput.accept(ModBlocks.ALUMINUM_ORE.get());
        pOutput.accept(ModBlocks.DEEPSLATE_ALUMINUM_ORE.get());
        pOutput.accept(ModBlocks.LEAD_BLOCK.get());
        pOutput.accept(ModBlocks.LEAD_ORE.get());
        pOutput.accept(ModBlocks.DEEPSLATE_LEAD_ORE.get());
        pOutput.accept(ModBlocks.ZINC_BLOCK.get());
        pOutput.accept(ModBlocks.ZINC_ORE.get());
        pOutput.accept(ModBlocks.DEEPSLATE_ZINC_ORE.get());

        pOutput.accept(ModItems.ALUMINUM_INGOT.get());
        pOutput.accept(ModItems.RAW_ALUMINUM.get());
        pOutput.accept(ModItems.ALUMINUM_DUST.get());
        pOutput.accept(ModItems.ALUMINUM_NUGGET.get());

        pOutput.accept(ModItems.LEAD_INGOT.get());
        pOutput.accept(ModItems.RAW_LEAD.get());
        pOutput.accept(ModItems.LEAD_DUST.get());
        pOutput.accept(ModItems.LEAD_NUGGET.get());

        pOutput.accept(ModItems.ZINC_INGOT.get());
        pOutput.accept(ModItems.RAW_ZINC.get());
        pOutput.accept(ModItems.ZINC_DUST.get());
        pOutput.accept(ModItems.ZINC_NUGGET.get());
      })
      .build());


  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}