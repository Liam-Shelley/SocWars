package com.soc.itemsrework;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.game.manager.AbstractHidingGameManager;
import com.soc.game.manager.GamesManager;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModifyEquipmentFunction;
import com.soc.materials.ToolMaterials;
import com.soc.mixin.GetAbsorptionAmountDataTracker;
import com.soc.util.DamageTypes;
import com.soc.util.ModBlockTags;
import com.soc.util.Sounds;
import com.soc.util.SphereExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.soc.items.util.ItemGroups.registerAndGroup;
import static com.soc.itemsrework.components.ModItemComponentsReworked.DAMAGE_TYPE;
import static com.soc.itemsrework.components.ModItemComponentsReworked.POST_HIT;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.lib.SocWarsLib.isBlockHidden;
import static com.soc.util.SphereExplosion.fireExplosion;

public interface ModItemsReworked {
	Item LIFETHIEF = registerAndGroup("lifethief", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.LIFETHIEF, 2.5f, -2.2f)
			.component(POST_HIT, (stack, target, attacker) -> {
				final int bonusHealth = attacker.getWorld().random.nextBetween(-1, 2);

				if (bonusHealth >= 0) {
					attacker.heal(bonusHealth);
				} else {
					final World world = attacker.getWorld();
					if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
						final DamageSource damageSource = damageSource(world, DamageTypes.LIFETHIEF);
						attacker.damage(serverWorld, damageSource, 1.0f);
					}
				}
			}),
		ItemGroups.ITEMS_KEY
	);
	Item DEVASTATOR_PRIME = ModItemsReworked.register("devastator_prime", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.EPIC)
			.sword(ToolMaterials.DEVASTATOR, 14.5f, -2.9f)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
			.component(POST_HIT, (stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 3 * 20, 1));
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
			})
	);
	Item NETHERWRONG_SWORD = ModItemsReworked.register("netherwrong_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterial.NETHERITE, -3f, -3f)
			.component(POST_HIT, (stack, target, attacker) -> {
				final World world = attacker.getWorld();
				if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
					final DamageSource damageSource = damageSource(world, DamageTypes.NETHERWRONG_SWORD);
					attacker.damage(serverWorld, damageSource, 8f);
				}
			})
	);
	Item ORANGE_SWORD = ModItemsReworked.register("orange_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5f, -2.1f)
			.maxDamage(500)
			.component(POST_HIT, (stack, target, attacker) -> {
				attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.VINE_BOOM, SoundCategory.PLAYERS, 1f, 1f);
			})
	);
	Item BLUE_SWORD = ModItemsReworked.register("blue_sword", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 8f, -2.15f)
			.maxDamage(800)
			.component(POST_HIT, (stack, target, attacker) -> {
				if (attacker.getWorld() instanceof ServerWorld serverWorld) {
					final SlimeEntity slime = new SlimeEntity(EntityType.SLIME, attacker.getWorld());
					slime.setSize(2, true);
					slime.setPosition(target.getPos());
					serverWorld.spawnEntity(slime);
				}
			})
	);
	Item PINK_SWORD = ModItemsReworked.register("pink_sword", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.EPIC)
			.sword(ToolMaterials.BASE, 10f, -2.2f)
			.maxDamage(1000)
			.component(POST_HIT, (stack, target, attacker) -> {
				float newAbsorption = attacker.getAbsorptionAmount() + 2;
				attacker.getDataTracker().set(GetAbsorptionAmountDataTracker.getAbsorption_Amount(), newAbsorption);
			})
	);
	Item KNOCKFORWARD_SWORD = ModItemsReworked.register("knockforward_sword", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 5.5f, -2.2f)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
			.maxDamage(450)
			.component(POST_HIT, (stack, target, attacker) -> {
				final Vec3d velocity = attacker.getRotationVector().multiply(-1.25f);

				if (target instanceof ServerPlayerEntity serverPlayer) {
					serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer.getId(), serverPlayer.getVelocity().add(velocity)));
				} else {
					target.addVelocity(velocity);
				}
			})
			.component(DataComponentTypes.LORE, new LoreComponent(List.of(
					Text.translatable("tooltip.knockforward_sword").formatted(Formatting.GOLD),
					Text.translatable("tooltip.knockforward_sword.knockback").formatted(Formatting.GRAY)
			))));
	Item TRANSPORTAS = ModItemsReworked.register("transportas", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.BASE, 5.5f, -2f)
			.maxDamage(450)
			.component(POST_HIT, (stack, target, attacker) -> {
				if (target.getWorld().random.nextBetween(1, 4) == 1) {
					target.setPosition(target.getPos().add(target.getRotationVector().multiply(3f)));
				}
			})
	);
	Item STORMAGEDDON = ModItemsReworked.register("stormageddon", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 5f, -2.1f)
			.maxDamage(500)
			.component(POST_HIT, (stack, target, attacker) -> {
				final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
				lightning.setPosition(target.getPos());
				target.getWorld().spawnEntity(lightning);
			})
			.component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("tooltip.stormageddon"))))
	);
	Item DETONATOR = ModItemsReworked.register("detonator", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.BASE, 4.5f, -2.2f)
			.maxDamage(400)
			.component(POST_HIT, (stack, target, attacker) -> {
				SphereExplosion.explode(target.getWorld(), target.getPos(), 2.5f, 0.5f, 1.4f, 0.75f, true, attacker, null);
			})
	);
	Item SHATTERSTAR = ModItemsReworked.register("shatterstar", SocWarsItem::new, new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.component(POST_HIT, (stack, target, attacker) -> {
				modifyEquipment(target, attacker, ReplaceMode.PRESENT,
						(targetEntity, slot) -> target.equipStack(slot, ItemStack.EMPTY),
						(targetEntity, slot) -> target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY));
			})
	);
	Item LEATHERER = ModItemsReworked.register("leatherer", SocWarsItem::new, new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.component(POST_HIT, (stack, target, attacker) -> {
				modifyEquipment(target, attacker, ReplaceMode.NOT_LEATHER, (targetEntity, slot) -> {
					final ItemStack item = leatherArmour(slot).getDefaultStack();
					item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.BINDING_CURSE), 1);
					item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.VANISHING_CURSE), 1);

					target.equipStack(slot, item);
				}, null);
			})
	);
	Item SPRING_SWORD = ModItemsReworked.register("spring_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 8f, -1.2f)
			.maxDamage(350)
			.rarity(Rarity.RARE)
			.component(POST_HIT, (stack, target, attacker) -> {
				if (attacker.getWorld().random.nextBetween(1, attacker.isOnGround() ? 6 : 4) == 1) {
					final ItemEntity item = attacker.dropItem(stack, true, false);
					if (item == null) return;
					item.setVelocity(attacker.getRotationVector().multiply(0.3f));

					attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
				}
			})
			.component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("tooltip.spring_sword").formatted(Formatting.YELLOW))))
	);
	Item FLESHY_BLADE = ModItemsReworked.register("fleshy_blade", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 6f, -2.2f)
			.component(POST_HIT, (stack, target, attacker) -> {
				attacker.getWorld().playSound(null, target.getBlockPos(), Sounds.FLESH, SoundCategory.MASTER, 1f, 1f);
			})
	);
	Item FIRESTORM = ModItemsReworked.register("firestorm", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5.5f, -2.5f)
			.maxDamage(400)
			.rarity(Rarity.RARE)
			.component(POST_HIT, (stack, target, attacker) -> {
				fireExplosion(target.getWorld(), target.getBlockPos(), 4f, attacker);
			})
	);
	Item CORRUPTED_SWORD = ModItemsReworked.register("corrupted_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5.5f, -2.3f)
			.maxDamage(550)
			.rarity(Rarity.UNCOMMON)
			.component(POST_HIT, (stack, target, attacker) -> {
				final World world = target.getWorld();
				iterateInSphere(target.getBlockPos(), 4, 0, pos -> {
					if (world.random.nextFloat() < 0.2f && !isBlockHidden(world, pos) && !world.getBlockState(pos).isIn(ModBlockTags.IMMUNE)) {
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				});
			})
	);
	Item POSTURA = ModItemsReworked.register("postura", SocWarsItem::new, new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.UNCOMMON)
			.component(POST_HIT, (stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 40 * 20, 0, false, true));
				stack.decrementUnlessCreative(1, attacker);
			})
			.component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("tooltip.postura"))))
	);
	Item FULL_METAL_SWORD = ModItemsReworked.register("full_metal_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 4.5f, -2.3f)
			.maxDamage(500)
			.component(POST_HIT, (stack, target, attacker) -> {
				attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.GET_SOME, SoundCategory.PLAYERS, 1f, 1f);
			})
			.component(DAMAGE_TYPE, DamageTypes.FULL_METAL_SWORD)
			.component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("tooltip.full_metal_sword"))))
	);
	Item SEEKING_STICK = ModItemsReworked.register("seeking_stick", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.attributeModifiers(new AttributeModifiersComponent(List.of(
					new AttributeModifiersComponent.Entry(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_range"), 1d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getDefault()),
					new AttributeModifiersComponent.Entry(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_speed"), 0.05d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getHidden()),
					new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_damage"), 6.5d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
			)))
			.component(POST_HIT, (stack, target, attacker) -> {
				GamesManager.getInstance().getGame(target).ifPresent(game -> {
					if (game instanceof AbstractHidingGameManager<?, ?, ?> hidingGameManager) {
						hidingGameManager.tryFindPlayer(attacker, (ServerPlayerEntity)target);
					}
				});
			})
			.component(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("tooltip.seeking_stick"))))
	);

	static void initialise() {}

	static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		// Create the item key.
		final RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));

		// Create the item instance.
		final Item item = itemFactory.apply(settings.registryKey(itemKey));

		// Register the item.
		Registry.register(Registries.ITEM, itemKey, item);

		return item;
	}




	//region Garbage section filled with all the code that I don't want to think about for now
	enum ReplaceMode {
		PRESENT,
		NOT_LEATHER
	}

	static void modifyEquipment(LivingEntity target, LivingEntity attacker, ReplaceMode replaceMode, ModifyEquipmentFunction armourFunction, ModifyEquipmentFunction handFunction) {
		final ArrayList<EquipmentSlot> armour = new ArrayList<>();
		for (EquipmentSlot slot : ARMOUR_SLOTS) {
			if (replaceMode == ReplaceMode.PRESENT ? target.hasStackEquipped(slot) : target.getEquippedStack(slot).getItem() != leatherArmour(slot)) { //Maybe lambda-ify this
				armour.add(slot);
			}
		}

		if (!armour.isEmpty() || target.getStackInHand(Hand.MAIN_HAND) != ItemStack.EMPTY) {
			target.playSound(SoundEvents.ENTITY_ITEM_BREAK.value());
			attacker.playSound(SoundEvents.ENTITY_ITEM_BREAK.value());

			attacker.getStackInHand(Hand.MAIN_HAND).decrementUnlessCreative(1, attacker);
		}

		if (!armour.isEmpty()) {
			armourFunction.modifyEquipment(target, armour.get(target.getWorld().random.nextBetween(0, armour.size() - 1)));
		} else {
			handFunction.modifyEquipment(target, null);
		}
	}
	//endregion
}
