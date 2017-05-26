package org.silvercatcher.reforged.items.weapons;

import org.silvercatcher.reforged.ReforgedMod;
import org.silvercatcher.reforged.api.IZombieEquippable;
import org.silvercatcher.reforged.api.ItemExtension;
import org.silvercatcher.reforged.material.MaterialDefinition;
import org.silvercatcher.reforged.material.MaterialManager;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemKatana extends ItemSword implements ItemExtension, IZombieEquippable {

	protected final MaterialDefinition materialDefinition;
	protected final boolean unbreakable;

	public ItemKatana(ToolMaterial material) {
		this(material, false);
	}

	public ItemKatana(ToolMaterial material, boolean unbreakable) {
		super(material);

		this.unbreakable = unbreakable;
		materialDefinition = MaterialManager.getMaterialDefinition(material);

		setUnlocalizedName(materialDefinition.getPrefixedName("katana"));
		setMaxDamage(materialDefinition.getMaxUses());
		setMaxStackSize(1);
		setCreativeTab(ReforgedMod.tabReforged);
	}

	@Override
	public Multimap getAttributeModifiers(ItemStack stack) {
		return ItemExtension.super.getAttributeModifiers(stack);
	}

	@Override
	public float getHitDamage() {
		return materialDefinition.getDamageVsEntity() + 2f;
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {
		return materialDefinition.getEnchantability();
	}

	public ToolMaterial getMaterial() {

		return materialDefinition.getMaterial();
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {

		if (!super.hitEntity(stack, target, attacker)) {

			int armorvalue = 0;

			for (int i = 3; i < 6; i++) {

				ItemStack armorStack = target.getItemStackFromSlot(EntityEquipmentSlot.values()[i]);
				if (armorStack != null && !armorStack.isEmpty() && armorStack.getItem() instanceof ItemArmor) {
					armorvalue += ((ItemArmor) armorStack.getItem()).damageReduceAmount;
				}
			}

			float damage = getHitDamage();

			if (attacker instanceof EntityPlayer)
				damage = damage + getEnchantmentBonus(stack, (EntityPlayer) attacker, target);

			if (armorvalue < 12) {

				damage *= 1.5f;
				target.hurtResistantTime = 0;
			}

			if (armorvalue > 6) {

				stack.damageItem(1, target);
			}

			target.attackEntityFrom(getDamage(attacker), damage);
		}

		return true;
	}

	@Override
	public boolean isDamageable() {
		if (unbreakable)
			return false;
		else
			return true;
	}

	@Override
	public void registerRecipes() {

		GameRegistry.addRecipe(new ItemStack(this), "  m", " m ", "s  ", 'm', materialDefinition.getRepairMaterial(),
				's', Items.STICK);
	}

	@Override
	public float zombieSpawnChance() {
		switch (materialDefinition.getMaterial()) {
		case GOLD:
			return 1;
		case IRON:
			return 2;
		case STONE:
			return 3;
		case WOOD:
			return 4;
		default:
			return 0;
		}
	}
}
