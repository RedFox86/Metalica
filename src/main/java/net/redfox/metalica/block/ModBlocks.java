package net.redfox.metalica.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.Metalica;
import net.redfox.metalica.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Metalica.MOD_ID);

  public static final RegistryObject<Block> ALUMINUM_BLOCK = registerBlock("aluminum_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
  public static final RegistryObject<Block> ALUMINUM_ORE = registerBlock("aluminum_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
  public static final RegistryObject<Block> DEEPSLATE_ALUMINUM_ORE = registerBlock("deepslate_aluminum_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));

  public static final RegistryObject<Block> LEAD_BLOCK = registerBlock("lead_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
  public static final RegistryObject<Block> LEAD_ORE = registerBlock("lead_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_ORE)));
  public static final RegistryObject<Block> DEEPSLATE_LEAD_ORE = registerBlock("deepslate_lead_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE)));

  public static final RegistryObject<Block> ZINC_BLOCK = registerBlock("zinc_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)));
  public static final RegistryObject<Block> ZINC_ORE = registerBlock("zinc_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.GOLD_ORE)));
  public static final RegistryObject<Block> DEEPSLATE_ZINC_ORE = registerBlock("deepslate_zinc_ore", () -> new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_GOLD_ORE)));

  private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
    RegistryObject<T> toReturn = BLOCKS.register(name, block);
    registerBlockItem(name, toReturn);
    return toReturn;
  }

  private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
    return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
  }

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}