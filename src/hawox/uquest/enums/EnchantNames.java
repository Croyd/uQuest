package hawox.uquest.enums;

public enum EnchantNames {
	PROTECTION_ENVIRONMENTAL("Protection"),
	PROTECTION_FIRE("Fire Protection"),
	PROTECTION_FALL("Feather Falling"),
	PROTECTION_EXPLOSIONS("Blast Protection"),
	PROTECTION_PROJECTILE("Projectile Protection"),
	OXYGEN("Respiration"),
	WATER_WORKER("Aqua Affinity"),
	DAMAGE_ALL("Sharpness"),
	DAMAGE_UNDEAD("Smite"),
	DAMAGE_ARTHROPODS("Bane of Arthropods"),
	KNOCKBACK("Knockback"),
	FIRE_ASPECT("Fire Aspect"),
	LOOT_BONUS_MOBS("Looting"),
	DIG_SPEED("Efficiency"),
	SILK_TOUCH("Silk Touch"),
	DURABILITY("Unbreaking"),
	LOOT_BONUS_BLOCKS("Fortune"),
	ARROW_DAMAGE("Power"),
	ARROW_KNOCKBACK("Punch"),
	ARROW_FIRE("Flame"),
	ARROW_INFINITE("Infinity");
	 
	 private String name;
	 
	 private EnchantNames(String c) {
	   name = c;
	 }
	 
	 public String getName() {
	   return name;
	 }
}