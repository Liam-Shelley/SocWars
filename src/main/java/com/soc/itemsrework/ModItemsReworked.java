package com.soc.itemsrework;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.game.manager.AbstractHidingGameManager;
import com.soc.game.manager.GamesManager;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.soc.items.util.ModItemGroups.registerAndGroup;
import static com.soc.itemsrework.components.ModItemComponentsReworked.DAMAGE_TYPE;
import static com.soc.itemsrework.components.ModItemComponentsReworked.ITEM_HIT;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.lib.SocWarsLib.isBlockHidden;
import static com.soc.util.SphereExplosion.fireExplosion;

public interface ModItemsReworked {
	Item BAT = registerAndGroup("bat", Item::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 4f, -1.9f)
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.bat"))),
			ItemGroups.COMBAT
	);
	Item BARBED_WIRE_BAT = registerAndGroup("barbed_wire_bat", Item::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5f, -1.95f),
			ItemGroups.COMBAT
	);
	Item NAILED_BAT = registerAndGroup("nailed_bat", Item::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 6f, -2f)
			.rarity(Rarity.UNCOMMON),
			ItemGroups.COMBAT
	);
	Item RAZOR_WIRE_BAT = registerAndGroup("razor_wire_bat", Item::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 7.5f, -2.1f)
			.rarity(Rarity.RARE),
			ItemGroups.COMBAT
	);
	Item SAW_BLADE_BAT = registerAndGroup("saw_blade_bat", Item::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 9f, -2.4f)
			.rarity(Rarity.RARE)
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.saw_blade_bat"))),
			ItemGroups.COMBAT
	);
	Item DEVASTATOR = registerAndGroup("devastator", Item::new, new Item.Settings()
			.sword(ToolMaterials.DEVASTATOR, 4f, -3.5f)
			.rarity(Rarity.RARE),
			ItemGroups.COMBAT
	);
	Item NETHERIGHT_SWORD = registerAndGroup("netheright_sword", Item::new, new Item.Settings()
			.sword(ToolMaterial.NETHERITE, -4.5f, 12f),
			ItemGroups.COMBAT
	);
	Item LIFETHIEF = registerAndGroup("lifethief", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.LIFETHIEF, 2.5f, -2.2f)
			.component(ITEM_HIT, (stack, target, attacker) -> {
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
			ItemGroups.COMBAT
	);
	Item DEVASTATOR_PRIME = registerAndGroup("devastator_prime", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.EPIC)
			.sword(ToolMaterials.DEVASTATOR, 14.5f, -2.9f)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 3 * 20, 1));
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
			}),
			ItemGroups.COMBAT
	);
	Item NETHERWRONG_SWORD = registerAndGroup("netherwrong_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterial.NETHERITE, -3f, -3f)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				final World world = attacker.getWorld();
				if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
					final DamageSource damageSource = damageSource(world, DamageTypes.NETHERWRONG_SWORD);
					attacker.damage(serverWorld, damageSource, 8f);
				}
			}),
			ItemGroups.COMBAT
	);
	Item ORANGE_SWORD = registerAndGroup("orange_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5f, -2.1f)
			.maxDamage(500)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.VINE_BOOM, SoundCategory.PLAYERS, 1f, 1f);
			}),
			ItemGroups.COMBAT
	);
	Item BLUE_SWORD = registerAndGroup("blue_sword", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 8f, -2.15f)
			.maxDamage(800)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				if (attacker.getWorld() instanceof ServerWorld serverWorld) {
					final SlimeEntity slime = new SlimeEntity(EntityType.SLIME, attacker.getWorld());
					slime.setSize(2, true);
					slime.setPosition(target.getPos());
					serverWorld.spawnEntity(slime);
				}
			}),
			ItemGroups.COMBAT
	);
	Item PINK_SWORD = registerAndGroup("pink_sword", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.EPIC)
			.sword(ToolMaterials.BASE, 10f, -2.2f)
			.maxDamage(1000)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				float newAbsorption = attacker.getAbsorptionAmount() + 2;
				attacker.getDataTracker().set(GetAbsorptionAmountDataTracker.getAbsorption_Amount(), newAbsorption);
			}),
			ItemGroups.COMBAT
	);
	Item KNOCKFORWARD_SWORD = registerAndGroup("knockforward_sword", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 5.5f, -2.2f)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
			.maxDamage(450)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				final Vec3d velocity = attacker.getRotationVector().multiply(-1.25f);

				if (target instanceof ServerPlayerEntity serverPlayer) {
					serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer.getId(), serverPlayer.getVelocity().add(velocity)));
				} else {
					target.addVelocity(velocity);
				}
			})
			.component(DataComponentTypes.LORE, tooltip(
					Text.translatable("tooltip.knockforward_sword").formatted(Formatting.GOLD),
					Text.translatable("tooltip.knockforward_sword.knockback").formatted(Formatting.GRAY)
			)),
			ItemGroups.COMBAT
	);
	Item TRANSPORTAS = registerAndGroup("transportas", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.BASE, 5.5f, -2f)
			.maxDamage(450)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				if (target.getWorld().random.nextBetween(1, 4) == 1) {
					target.setPosition(target.getPos().add(target.getRotationVector().multiply(3f)));
				}
			}),
			ItemGroups.COMBAT
	);
	Item STORMAGEDDON = registerAndGroup("stormageddon", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 5f, -2.1f)
			.maxDamage(500)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
				lightning.setPosition(target.getPos());
				target.getWorld().spawnEntity(lightning);
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.stormageddon"))),
			ItemGroups.COMBAT
	);
	Item DETONATOR = registerAndGroup("detonator", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.BASE, 4.5f, -2.2f)
			.maxDamage(400)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				SphereExplosion.explode(target.getWorld(), target.getPos(), 2.5f, 0.5f, 1.4f, 0.75f, true, attacker, null);
			}),
			ItemGroups.COMBAT
	);
	Item SHATTERSTAR = registerAndGroup("shatterstar", SocWarsItem::new, new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				modifyEquipment(target, attacker, ReplaceMode.PRESENT,
						(targetEntity, slot) -> target.equipStack(slot, ItemStack.EMPTY),
						(targetEntity, slot) -> target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY));
			}),
			ItemGroups.COMBAT
	);
	Item LEATHERER = registerAndGroup("leatherer", SocWarsItem::new, new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				modifyEquipment(target, attacker, ReplaceMode.NOT_LEATHER, (targetEntity, slot) -> {
					final ItemStack item = leatherArmour(slot).getDefaultStack();
					item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.BINDING_CURSE), 1);
					item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.VANISHING_CURSE), 1);

					target.equipStack(slot, item);
				}, null);
			}),
			ItemGroups.COMBAT
	);
	Item SPRING_SWORD = registerAndGroup("spring_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 8f, -1.2f)
			.maxDamage(350)
			.rarity(Rarity.RARE)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				if (attacker.getWorld().random.nextBetween(1, attacker.isOnGround() ? 6 : 4) == 1) {
					final ItemEntity item = attacker.dropItem(stack, true, false);
					if (item == null) return;
					item.setVelocity(attacker.getRotationVector().multiply(0.3f));

					attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
				}
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.spring_sword").formatted(Formatting.YELLOW))),
			ItemGroups.COMBAT
	);
	Item FLESHY_BLADE = registerAndGroup("fleshy_blade", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 6f, -2.2f)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				attacker.getWorld().playSound(null, target.getBlockPos(), Sounds.FLESH, SoundCategory.MASTER, 1f, 1f);
			}),
			ItemGroups.COMBAT
	);
	Item FIRESTORM = registerAndGroup("firestorm", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5.5f, -2.5f)
			.maxDamage(400)
			.rarity(Rarity.RARE)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				fireExplosion(target.getWorld(), target.getBlockPos(), 4f, attacker);
			}),
			ItemGroups.COMBAT
	);
	Item CORRUPTED_SWORD = registerAndGroup("corrupted_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 5.5f, -2.3f)
			.maxDamage(550)
			.rarity(Rarity.UNCOMMON)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				final World world = target.getWorld();
				iterateInSphere(target.getBlockPos(), 4, 0, pos -> {
					if (world.random.nextFloat() < 0.2f && !isBlockHidden(world, pos) && !world.getBlockState(pos).isIn(ModBlockTags.IMMUNE)) {
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				});
			}),
			ItemGroups.COMBAT
	);
	Item POSTURA = registerAndGroup("postura", SocWarsItem::new, new Item.Settings()
			.maxCount(1)
			.rarity(Rarity.UNCOMMON)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 40 * 20, 0, false, true));
				stack.decrementUnlessCreative(1, attacker);
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.postura"))),
			ItemGroups.COMBAT
	);
	Item FULL_METAL_SWORD = registerAndGroup("full_metal_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 4.5f, -2.3f)
			.maxDamage(500)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.GET_SOME, SoundCategory.PLAYERS, 1f, 1f);
			})
			.component(DAMAGE_TYPE, DamageTypes.FULL_METAL_SWORD)
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.full_metal_sword"))),
			ItemGroups.COMBAT
	);
	Item SEEKING_STICK = registerAndGroup("seeking_stick", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.attributeModifiers(new AttributeModifiersComponent(List.of(
					new AttributeModifiersComponent.Entry(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_range"), 1d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getDefault()),
					new AttributeModifiersComponent.Entry(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_speed"), 0.05d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getHidden()),
					new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_damage"), 6.5d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
			)))
			.component(ITEM_HIT, (stack, target, attacker) -> {
				GamesManager.getInstance().getGame(target).ifPresent(game -> {
					if (game instanceof AbstractHidingGameManager<?, ?, ?> hidingGameManager) {
						hidingGameManager.tryFindPlayer(attacker, (ServerPlayerEntity)target);
					}
				});
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.seeking_stick"))),
			ItemGroups.COMBAT
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




	static LoreComponent tooltip(Text... lines) {
		final List<Text> linesList = List.of(lines);
		return new LoreComponent(linesList, linesList);
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

	private static void shootEntity(PlayerEntity user, Hand hand, int missDamage, int hitDamage, int hitCooldown, Consumer<LivingEntity> effect) {
		final EntityHitResult hit = ProjectileUtil.raycast(
				user,
				user.getEyePos(),
				user.getEyePos().add(user.getRotationVector().multiply(250d)),
				user.getBoundingBox().stretch(user.getRotationVector().multiply(250d)),
				entity -> entity instanceof LivingEntity,
				250f * 250f
		);

		final ItemStack stack = user.getStackInHand(hand);

		if (hit == null) {
			stack.damage(missDamage, user, hand);
			user.getItemCooldownManager().set(user.getStackInHand(hand), 2);
		} else {
			stack.damage(hitDamage, user, hand);
			user.getItemCooldownManager().set(user.getStackInHand(hand), hitCooldown);

			effect.accept((LivingEntity)hit.getEntity()); //I hereby declare this cast safe because I already filter for living entities
		}
	}

	private static void dash(PlayerEntity user, Hand hand, float strength) {
		final float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
		final float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
		final float dashStrength = (float)Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * strength;

		user.addVelocity(user.getRotationVector().multiply(dashStrength));

		user.getStackInHand(hand).damage(Math.round(pitchStrength * 10), user, hand);
	}

	private static BlockPos shootScroll(World world, PlayerEntity user, Hand hand, Consumer<BlockPos> blockPosConsumer) {
		final BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVector().multiply(25f)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, user));
		final ItemStack stack = user.getStackInHand(hand);

		if (hit != null && !world.isAir(hit.getBlockPos())) {
			iterateInSphere(hit.getBlockPos(), 4.5f, 0f, blockPosConsumer);
			stack.damage(3, user, hand);
		}

		stack.damage(1, user, hand);

		return mapIfNotNull(hit, BlockHitResult::getBlockPos);
	}
	//endregion
}
