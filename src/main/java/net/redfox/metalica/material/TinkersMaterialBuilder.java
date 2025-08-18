/* (C)2025 */
package net.redfox.metalica.material;

import java.util.ArrayList;
import net.minecraft.world.item.Tier;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.tools.stats.*;

public class TinkersMaterialBuilder {
  private final ArrayList<IMaterialStats> statList = new ArrayList<>();

  private HeadMaterialStats headStats;
  private HandleMaterialStats handleStats;
  private PlatingMaterialStats helmetStats;
  private PlatingMaterialStats chestplateStats;
  private PlatingMaterialStats leggingsStats;
  private PlatingMaterialStats bootsStats;
  private PlatingMaterialStats shieldStats;
  private GripMaterialStats gripStats;
  private LimbMaterialStats limbStats;
  private StatlessMaterialStats binding;
  private StatlessMaterialStats bowstring;
  private StatlessMaterialStats maille;
  private StatlessMaterialStats shieldCore;
  private StatlessMaterialStats repairKit;
  private String modifier;
  private final Tier tier;
  private final int temperature;

  public TinkersMaterialBuilder(Tier tier, int temperature) {
    this.tier = tier;
    this.temperature = temperature;
  }

  public TinkersMaterialBuilder modifier(String modifier) {
    this.modifier = modifier;
    return this;
  }

  public TinkersMaterialBuilder head(int durability, float miningSpeed, float attackDamage) {
    this.headStats = new HeadMaterialStats(durability, miningSpeed, tier, attackDamage);
    statList.add(headStats);
    return this;
  }

  public TinkersMaterialBuilder handle(
      float durability, float miningSpeed, float meleeSpeed, float attackDamage) {
    this.handleStats = new HandleMaterialStats(durability, miningSpeed, meleeSpeed, attackDamage);
    statList.add(handleStats);
    return this;
  }

  public TinkersMaterialBuilder helmet(
      int durability, float armor, float armorToughness, float knockbackResistance) {
    this.helmetStats =
        new PlatingMaterialStats(
            PlatingMaterialStats.HELMET, durability, armor, armorToughness, knockbackResistance);
    statList.add(helmetStats);
    return this;
  }

  public TinkersMaterialBuilder chestplate(
      int durability, float armor, float armorToughness, float knockbackResistance) {
    this.chestplateStats =
        new PlatingMaterialStats(
            PlatingMaterialStats.CHESTPLATE,
            durability,
            armor,
            armorToughness,
            knockbackResistance);
    statList.add(chestplateStats);
    return this;
  }

  public TinkersMaterialBuilder leggings(
      int durability, float armor, float armorToughness, float knockbackResistance) {
    this.leggingsStats =
        new PlatingMaterialStats(
            PlatingMaterialStats.LEGGINGS, durability, armor, armorToughness, knockbackResistance);
    statList.add(leggingsStats);
    return this;
  }

  public TinkersMaterialBuilder boots(
      int durability, float armor, float armorToughness, float knockbackResistance) {
    this.bootsStats =
        new PlatingMaterialStats(
            PlatingMaterialStats.BOOTS, durability, armor, armorToughness, knockbackResistance);
    statList.add(bootsStats);
    return this;
  }

  public TinkersMaterialBuilder shield(
      int durability, float armor, float armorToughness, float knockbackResistance) {
    this.shieldStats =
        new PlatingMaterialStats(
            PlatingMaterialStats.SHIELD, durability, armor, armorToughness, knockbackResistance);
    statList.add(shieldStats);
    return this;
  }

  public TinkersMaterialBuilder grip(float durability, float accuracy, float meleeDamage) {
    this.gripStats = new GripMaterialStats(durability, accuracy, meleeDamage);
    statList.add(gripStats);
    return this;
  }

  public TinkersMaterialBuilder limb(
      int durability, float drawSpeed, float velocity, float accuracy) {
    this.limbStats = new LimbMaterialStats(durability, drawSpeed, velocity, accuracy);
    statList.add(limbStats);
    return this;
  }

  public TinkersMaterialBuilder binding() {
    this.binding = StatlessMaterialStats.BINDING;
    statList.add(binding);
    return this;
  }

  public TinkersMaterialBuilder bowstring() {
    this.bowstring = StatlessMaterialStats.BOWSTRING;
    statList.add(bowstring);
    return this;
  }

  public TinkersMaterialBuilder maille() {
    this.maille = StatlessMaterialStats.MAILLE;
    statList.add(maille);
    return this;
  }

  public TinkersMaterialBuilder sheildCore() {
    this.shieldCore = StatlessMaterialStats.SHIELD_CORE;
    statList.add(shieldCore);
    return this;
  }

  public TinkersMaterialBuilder repairKit() {
    this.repairKit = StatlessMaterialStats.REPAIR_KIT;
    statList.add(repairKit);
    return this;
  }

  public Tier getTier() {
    return tier;
  }

  public int getTemperature() {
    return temperature;
  }

  public String getModifier() {
    return modifier;
  }

  public ArrayList<IMaterialStats> getStatList() {
    return statList;
  }
}
