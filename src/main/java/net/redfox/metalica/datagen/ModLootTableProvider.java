/* (C)2025 */
package net.redfox.metalica.datagen;

import java.util.List;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.redfox.metalica.datagen.loot.ModBlockLootTables;

public class ModLootTableProvider {
  public static LootTableProvider create(PackOutput output) {
    return new LootTableProvider(
        output,
        Set.of(),
        List.of(
            new LootTableProvider.SubProviderEntry(
                ModBlockLootTables::new, LootContextParamSets.BLOCK)));
  }
}
