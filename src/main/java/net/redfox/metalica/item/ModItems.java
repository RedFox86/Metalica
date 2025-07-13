package net.redfox.metalica.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.redfox.metalica.Metalica;

public class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Metalica.MOD_ID);

  public static final RegistryObject<Item> ALUMINUM_INGOT = ITEMS.register("aluminum_ingot", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> RAW_ALUMINUM = ITEMS.register("raw_aluminum", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> ALUMINUM_DUST = ITEMS.register("aluminum_dust", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> ALUMINUM_NUGGET = ITEMS.register("aluminum_nugget", () -> new Item(new Item.Properties()));

  public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> RAW_LEAD = ITEMS.register("raw_lead", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> LEAD_DUST = ITEMS.register("lead_dust", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> LEAD_NUGGET = ITEMS.register("lead_nugget", () -> new Item(new Item.Properties()));

  public static final RegistryObject<Item> ZINC_INGOT = ITEMS.register("zinc_ingot", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> RAW_ZINC = ITEMS.register("raw_zinc", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> ZINC_DUST = ITEMS.register("zinc_dust", () -> new Item(new Item.Properties()));
  public static final RegistryObject<Item> ZINC_NUGGET = ITEMS.register("zinc_nugget", () -> new Item(new Item.Properties()));

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}