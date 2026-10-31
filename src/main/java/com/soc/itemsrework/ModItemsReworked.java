package com.soc.itemsrework;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.entities.*;
import com.soc.game.manager.AbstractHidingGameManager;
import com.soc.game.manager.GamesManager;
import com.soc.game.map.DyeColourWithEmpty;
import com.soc.items.DepositableChestBlockItem;
import com.soc.items.DepositableEnderChestBlockItem;
import com.soc.items.components.CommandFunctionComponent;
import com.soc.items.util.*;
import com.soc.itemsrework.components.ExponComponent;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.IntBox;
import com.soc.materials.ToolMaterials;
import com.soc.mixin.GetAbsorptionAmountDataTracker;
import com.soc.networking.s2c.AddVelocityPayload;
import com.soc.networking.s2c.BatchParticlePayload;
import com.soc.player.Morph;
import com.soc.util.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.blocks.util.ModBlocks.CHEST_PLACEHOLDER;
import static com.soc.blocks.util.ModBlocks.MANY_BEDS_BLOCK;
import static com.soc.game.manager.AbstractGameManager.getBlockDamagePredicate;
import static com.soc.items.BowItem.playBowSound;
import static com.soc.player.Morph.attemptMorph;
import static com.soc.items.util.ModItemGroups.registerAndGroup;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.lib.SocWarsLib.isBlockHidden;
import static com.soc.util.SphereExplosion.fireExplosion;
import static net.minecraft.item.Items.ENCHANTED_GOLDEN_APPLE;
import static net.minecraft.item.Items.NETHERITE_SWORD;

@SuppressWarnings("PointlessArithmeticExpression")
public interface ModItemsReworked {
	//region some garbage that I need to move out
	SoundEvent[] TAUNTS = {
			SoundEvents.BLOCK_ANVIL_USE,
			SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER,
			SoundEvents.BLOCK_BELL_USE,
			SoundEvents.ENTITY_FROG_DEATH,
			SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO.value(),
			SoundEvents.ENTITY_VILLAGER_YES,
			SoundEvents.ENTITY_VILLAGER_AMBIENT,
			SoundEvents.ENTITY_CHICKEN_HURT,
			SoundEvents.ENTITY_FOX_SCREECH,
			SoundEvents.ENTITY_COD_FLOP,
			SoundEvents.ENTITY_GENERIC_EXPLODE.value(),
			SoundEvents.ENTITY_GOAT_SCREAMING_AMBIENT,
			SoundEvents.ENTITY_GOAT_SCREAMING_DEATH,
			SoundEvents.ENTITY_GOAT_SCREAMING_PREPARE_RAM
	};
	EffectRecord[] ALCHEMISM_EFFECTS = {
			new EffectRecord(StatusEffects.INSTANT_DAMAGE, 0, 1),
			new EffectRecord(StatusEffects.INSTANT_HEALTH, 0, 1),
			new EffectRecord(StatusEffects.POISON, 1, 1 * 20),
			new EffectRecord(StatusEffects.WITHER, 1, 1 * 20),
			new EffectRecord(StatusEffects.SPEED, 0, 1 * 20),
			new EffectRecord(StatusEffects.REGENERATION, 1, 1 * 20),
			new EffectRecord(StatusEffects.BLINDNESS, 0, 1 * 20),
			new EffectRecord(StatusEffects.NAUSEA, 0, 1 * 20),
			new EffectRecord(StatusEffects.JUMP_BOOST, 1, 1 * 20),
			new EffectRecord(StatusEffects.DARKNESS, 0, 1 * 20),
			new EffectRecord(StatusEffects.SLOW_FALLING, 0, 1 * 20),
			new EffectRecord(StatusEffects.RESISTANCE, 1, 1 * 20),
	};
	EffectRecord[] GARBO_EFFECTS = {
			new EffectRecord(StatusEffects.SLOWNESS, 3),
			new EffectRecord(StatusEffects.MINING_FATIGUE, 1),
			new EffectRecord(StatusEffects.NAUSEA, 0),
			new EffectRecord(StatusEffects.BLINDNESS, 0),
			new EffectRecord(StatusEffects.HUNGER, 0),
			new EffectRecord(StatusEffects.WEAKNESS, 3),
			new EffectRecord(StatusEffects.POISON, 1),
			new EffectRecord(StatusEffects.GLOWING, 0),
	};
	Block[] BLOCKS = {
			Blocks.OAK_PLANKS,
			Blocks.BIRCH_PLANKS,
			Blocks.JUNGLE_PLANKS,
			Blocks.SPRUCE_PLANKS,
			Blocks.DARK_OAK_PLANKS,
			Blocks.ACACIA_PLANKS,
			Blocks.MANGROVE_PLANKS,
			Blocks.PALE_OAK_PLANKS,
			Blocks.WARPED_PLANKS,
			Blocks.CRIMSON_PLANKS,
			Blocks.BAMBOO_PLANKS,
			Blocks.CHERRY_PLANKS
	};
	//endregion







	Item BAT = registerAndGroup("bat", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 4f, -1.9f)
			.simpleTooltip(Text.translatable("tooltip.bat")),
			ItemGroups.COMBAT
	);
	Item BARBED_WIRE_BAT = registerAndGroup("barbed_wire_bat", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 5f, -1.95f),
			ItemGroups.COMBAT
	);
	Item NAILED_BAT = registerAndGroup("nailed_bat", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 6f, -2f)
			.rarity(Rarity.UNCOMMON),
			ItemGroups.COMBAT
	);
	Item RAZOR_WIRE_BAT = registerAndGroup("razor_wire_bat", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 7.5f, -2.1f)
			.rarity(Rarity.RARE),
			ItemGroups.COMBAT
	);
	Item SAW_BLADE_BAT = registerAndGroup("saw_blade_bat", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 9f, -2.4f)
			.rarity(Rarity.RARE)
			.simpleTooltip(Text.translatable("tooltip.saw_blade_bat")),
			ItemGroups.COMBAT
	);
	Item DEVASTATOR = registerAndGroup("devastator", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterials.DEVASTATOR, 4f, -3.5f)
			.rarity(Rarity.RARE),
			ItemGroups.COMBAT
	);
	Item NETHERIGHT_SWORD = registerAndGroup("netheright_sword", Item::new, new ExtendedItemSettings()
			.sword(ToolMaterial.NETHERITE, -4.5f, 12f),
			ItemGroups.COMBAT
	);
	Item LIFETHIEF = registerAndGroup("lifethief", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.LIFETHIEF, 2.5f, -2.2f)
			.itemHit((stack, target, attacker) -> {
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
	Item DEVASTATOR_PRIME = registerAndGroup("devastator_prime", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.EPIC)
			.sword(ToolMaterials.DEVASTATOR, 14.5f, -2.9f)
			.enchantmentGlint()
			.itemHit((stack, target, attacker) -> {
						target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 3 * 20, 1));
						target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 2));
					}),
			ItemGroups.COMBAT
	);
	Item NETHERWRONG_SWORD = registerAndGroup("netherwrong_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterial.NETHERITE, -3f, -3f)
			.itemHit((stack, target, attacker) -> {
				final World world = attacker.getWorld();
				if (attacker instanceof LivingEntity && world instanceof ServerWorld serverWorld) {
					final DamageSource damageSource = damageSource(world, DamageTypes.NETHERWRONG_SWORD);
					attacker.damage(serverWorld, damageSource, 8f);
				}
			}),
			ItemGroups.COMBAT
	);
	Item ORANGE_SWORD = registerAndGroup("orange_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 5f, -2.1f)
			.maxDamage(500)
			.itemHit((stack, target, attacker) -> {
				attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.VINE_BOOM, SoundCategory.PLAYERS, 1f, 1f);
			}),
			ItemGroups.COMBAT
	);
	Item BLUE_SWORD = registerAndGroup("blue_sword", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 8f, -2.15f)
			.maxDamage(800)
			.itemHit((stack, target, attacker) -> {
				if (attacker.getWorld() instanceof ServerWorld serverWorld) {
					final SlimeEntity slime = new SlimeEntity(EntityType.SLIME, attacker.getWorld());
					slime.setSize(2, true);
					slime.setPosition(target.getPos());
					serverWorld.spawnEntity(slime);
				}
			}),
			ItemGroups.COMBAT
	);
	Item PINK_SWORD = registerAndGroup("pink_sword", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.EPIC)
			.sword(ToolMaterials.BASE, 10f, -2.2f)
			.maxDamage(1000)
			.itemHit((stack, target, attacker) -> {
				float newAbsorption = attacker.getAbsorptionAmount() + 2;
				attacker.getDataTracker().set(GetAbsorptionAmountDataTracker.getAbsorption_Amount(), newAbsorption);
			}),
			ItemGroups.COMBAT
	);
	Item KNOCKFORWARD_SWORD = registerAndGroup("knockforward_sword", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 5.5f, -2.2f)
			.enchantmentGlint()
			.maxDamage(450)
			.itemHit((stack, target, attacker) -> {
				final Vec3d velocity = attacker.getRotationVector().multiply(-1.25f);

				if (target instanceof ServerPlayerEntity serverPlayer) {
					serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer.getId(), serverPlayer.getVelocity().add(velocity)));
				} else {
					target.addVelocity(velocity);
				}
			})
			.simpleTooltip(
					Text.translatable("tooltip.knockforward_sword").formatted(Formatting.GOLD),
					Text.translatable("tooltip.knockforward_sword.knockback").formatted(Formatting.GRAY)
			),
			ItemGroups.COMBAT
	);
	Item TRANSPORTAS = registerAndGroup("transportas", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.BASE, 5.5f, -2f)
			.maxDamage(450)
			.itemHit((stack, target, attacker) -> {
				if (target.getWorld().random.nextBetween(1, 4) == 1) {
					target.setPosition(target.getPos().add(target.getRotationVector().multiply(3f)));
				}
			}),
			ItemGroups.COMBAT
	);
	Item STORMAGEDDON = registerAndGroup("stormageddon", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.sword(ToolMaterials.BASE, 5f, -2.1f)
			.maxDamage(500)
			.itemHit((stack, target, attacker) -> {
				final LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, target.getWorld());
				lightning.setPosition(target.getPos());
				target.getWorld().spawnEntity(lightning);
			})
			.simpleTooltip(Text.translatable("tooltip.stormageddon")),
			ItemGroups.COMBAT
	);
	Item DETONATOR = registerAndGroup("detonator", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.RARE)
			.sword(ToolMaterials.BASE, 4.5f, -2.2f)
			.maxDamage(400)
			.itemHit((stack, target, attacker) -> {
				SphereExplosion.explode(target.getWorld(), target.getPos(), 2.5f, 0.5f, 1.4f, 0.75f, true, attacker, null);
			}),
			ItemGroups.COMBAT
	);
	Item SHATTERSTAR = registerAndGroup("shatterstar", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.itemHit((stack, target, attacker) -> {
				modifyEquipment(target, attacker, ReplaceMode.PRESENT,
						(targetEntity, slot) -> target.equipStack(slot, ItemStack.EMPTY),
						(targetEntity, slot) -> target.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY));
			}),
			ItemGroups.COMBAT
	);
	Item LEATHERER = registerAndGroup("leatherer", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.itemHit((stack, target, attacker) -> {
				modifyEquipment(target, attacker, ReplaceMode.NOT_LEATHER, (targetEntity, slot) -> {
					final ItemStack item = leatherArmour(slot).getDefaultStack();
					item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.BINDING_CURSE), 1);
					item.addEnchantment(enchantmentEntry(target.getWorld(), Enchantments.VANISHING_CURSE), 1);

					target.equipStack(slot, item);
				}, null);
			}),
			ItemGroups.COMBAT
	);
	Item SPRING_SWORD = registerAndGroup("spring_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 8f, -1.2f)
			.maxDamage(350)
			.rarity(Rarity.RARE)
			.itemHit((stack, target, attacker) -> {
				if (attacker.getWorld().random.nextBetween(1, attacker.isOnGround() ? 6 : 4) == 1) {
					final ItemEntity item = attacker.dropItem(stack, true, false);
					if (item == null) return;
					item.setVelocity(attacker.getRotationVector().multiply(0.3f));

					attacker.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
				}
			})
			.simpleTooltip(Text.translatable("tooltip.spring_sword").formatted(Formatting.YELLOW)),
			ItemGroups.COMBAT
	);
	Item FLESHY_BLADE = registerAndGroup("fleshy_blade", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 6f, -2.2f)
			.itemHit((stack, target, attacker) -> {
				attacker.getWorld().playSound(null, target.getBlockPos(), Sounds.FLESH, SoundCategory.MASTER, 1f, 1f);
			})
			.dynamicTooltip((stack, time) -> List.of(Text.translatable(time % 25 > 2 ? "tooltip.fleshy_blade" : "tooltip.fleshy_blade.wet").formatted(Formatting.RED))),
			ItemGroups.COMBAT
	);
	Item FIRESTORM = registerAndGroup("firestorm", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 5.5f, -2.5f)
			.maxDamage(400)
			.rarity(Rarity.RARE)
			.itemHit((stack, target, attacker) -> {
				fireExplosion(target.getWorld(), target.getBlockPos(), 4f, attacker);
			}),
			ItemGroups.COMBAT
	);
	Item CORRUPTED_SWORD = registerAndGroup("corrupted_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 5.5f, -2.3f)
			.maxDamage(550)
			.rarity(Rarity.UNCOMMON)
			.itemHit((stack, target, attacker) -> {
				final World world = target.getWorld();
				iterateInSphere(target.getBlockPos(), 4, 0, pos -> {
					if (world.random.nextFloat() < 0.2f && !isBlockHidden(world, pos) && !world.getBlockState(pos).isIn(ModBlockTags.IMMUNE)) {
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				});
			}),
			ItemGroups.COMBAT
	);
	Item POSTURA = registerAndGroup("postura", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(1)
			.rarity(Rarity.UNCOMMON)
			.itemHit((stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(ModEffects.ARTHRODESIS, 40 * 20, 0, false, true));
				stack.decrementUnlessCreative(1, attacker);
			})
			.simpleTooltip(Text.translatable("tooltip.postura")),
			ItemGroups.COMBAT
	);
	Item FULL_METAL_SWORD = registerAndGroup("full_metal_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 4.5f, -2.3f)
			.maxDamage(500)
			.itemHit((stack, target, attacker) -> {
				attacker.getWorld().playSound(null, attacker.getBlockPos(), Sounds.GET_SOME, SoundCategory.PLAYERS, 1f, 1f);
			})
			.damageType(DamageTypes.FULL_METAL_SWORD)
			.simpleTooltip(Text.translatable("tooltip.full_metal_sword")),
			ItemGroups.COMBAT
	);
	Item SEEKING_STICK = registerAndGroup("seeking_stick", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.attributeModifiers(new AttributeModifiersComponent(List.of(
					new AttributeModifiersComponent.Entry(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_range"), 1d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getDefault()),
					new AttributeModifiersComponent.Entry(EntityAttributes.MOVEMENT_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_speed"), 0.05d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND, AttributeModifiersComponent.Display.getHidden()),
					new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "seeking_stick_damage"), 6.5d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
			)))
			.itemHit((stack, target, attacker) -> {
				GamesManager.getInstance().getGame(target).ifPresent(game -> {
					if (game instanceof AbstractHidingGameManager<?, ?, ?> hidingGameManager) {
						hidingGameManager.tryFindPlayer(attacker, (ServerPlayerEntity)target);
					}
				});
			})
			.simpleTooltip(Text.translatable("tooltip.seeking_stick")),
			ItemGroups.COMBAT
	);
	Item DASHREND = registerAndGroup("dashrend", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.DASH, 2.5f, -2f)
			.useCooldown(3.5f)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				dash(user, hand, 0.8f);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item VELOCITY_STAFF = registerAndGroup("velocity_staff", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(300)
			.useCooldown(2f)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				dash(user, hand, 1.5f);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item VEXING_STAFF = registerAndGroup("vexing_staff", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(5)
			.useCooldown(45f)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				for (int i = 0; i < 2; i++) {
					final VexEntity vex = new VexEntity(EntityType.VEX, world);
					vex.setPosition(user.getEyePos().add(user.getRotationVector()));

					copyTeam(world, vex, user);

					world.spawnEntity(vex);

					user.getStackInHand(hand).damage(1, user, hand);
				}

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item YELLOW_SWORD = registerAndGroup("yellow_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 6f, -2.1f)
			.useCooldown(1.5f)
			.maxDamage(600)
			.itemUse((world, user, hand) -> {
				final ItemStack itemStack = user.getStackInHand(hand);

				if (world instanceof ServerWorld serverWorld) {
					SpectralArrowEntity arrow = ProjectileEntity.spawn(new SpectralArrowEntity(world, user, Items.AIR.getDefaultStack(), itemStack), serverWorld, itemStack);
					arrow.setPosition(user.getEyePos());
					arrow.setVelocity(user.getRotationVector().multiply(2f));
					arrow.setPitch(user.getPitch());
					arrow.setYaw(user.getYaw());
				}

				itemStack.damage(3, user, hand);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item GOD_COMPLEX = registerAndGroup("god_complex", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(5f)
			.enchantmentGlint()
			.rarity(Rarity.EPIC)
			.itemUse((world, user, hand) -> {
				user.addStatusEffect(new StatusEffectInstance(ModEffects.FLIGHT, 5 * 20, 0, false, false));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			})
			.simpleTooltip(Text.translatable("tooltip.god_complex")),
			ItemGroups.TOOLS
	);
	Item SCROLL_OF_EAU = registerAndGroup("scroll_of_eau", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(1f)
			.maxDamage(3)
			.rarity(Rarity.UNCOMMON)
			.itemUse((world, user, hand) -> {
				final @Nullable BlockPos hitPos = shootScroll(world, user, hand, pos -> {
					if (world.isAir(pos)) world.setBlockState(pos, Blocks.WATER.getDefaultState().with(Properties.LEVEL_15, 7));
				});
				ifNotNull(hitPos, pos -> world.setBlockState(pos.add(0,4,0), Blocks.WATER.getDefaultState()));

				return ActionResult.SUCCESS;
			})
			.simpleTooltip(Text.translatable("tooltip.scroll_of_eau")),
			ItemGroups.TOOLS
	);
	Item SCROLL_OF_HELLFIRE = registerAndGroup("scroll_of_hellfire", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(1f)
			.maxDamage(3)
			.rarity(Rarity.UNCOMMON)
			.itemUse((world, user, hand) -> {
				shootScroll(world, user, hand, pos -> {
					if (world.isAir(pos)) world.setBlockState(pos, Blocks.LAVA.getDefaultState());
				});

				return ActionResult.SUCCESS;
			})
			.simpleTooltip(Text.translatable("tooltip.scroll_of_hellfire")),
			ItemGroups.TOOLS
	);
	Item C_U_E_B = registerAndGroup("c_u_e_b", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(1f)
			.itemUse((world, user, hand) -> {
				final BlockPos centre = BlockPos.ofFloored(user.getEyePos().add(user.getRotationVector().multiply(20f)));

				iterateInCube(centre, 5, pos -> {
					if (world.isAir(pos)) world.setBlockState(pos, Blocks.IRON_BLOCK.getDefaultState());
				});

				world.playSound(null, centre, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS);

				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item SHRINK_RAY = registerAndGroup("shrink_ray", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(16)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2 * 0.5f));

				return ActionResult.SUCCESS;
			})
			.simpleTooltip(Text.translatable("tooltip.shrink_ray")),
			ItemGroups.TOOLS
	);
	Item BIGGENING_RAY = registerAndGroup("biggening_ray", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(16)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2));

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item THE_LINE = registerAndGroup("the_line", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(4)
			.useCooldown(2f)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				if (world.isClient) return ActionResult.SUCCESS;

				final boolean allowsMovementControl = user.isSneaking();
				final Vec3d cachedPosition = new Vec3d(user.getEyePos().toVector3f());

				final AtomicInteger beginContext = new AtomicInteger(7);
				final Vec3d cachedDirection = new Vec3d(user.getRotationVector().toVector3f());

				Coroutines.getInstance().startCoroutine(new Coroutine<>(beginContext, context -> {
					final int i = context.getAndIncrement();

					if (i >= 50) return true;
					final Vec3d pos = (allowsMovementControl ? user.getEyePos() : cachedPosition).add(cachedDirection.multiply(i));

					final TntEntity tnt = new TntEntity(world, pos.x, pos.y, pos.z, user);
					tnt.setFuse(20);
					world.spawnEntity(tnt);

					return !world.getBlockState(BlockPos.ofFloored(pos)).isAir();
				}));

				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item WHEATENATOR = registerAndGroup("wheatenator", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 4.5f, -2.4f)
			.maxDamage(350)
			.useCooldown(0.25f)
			.itemUse((world, user, hand) -> {
				final Vec3d rotationVec = user.getRotationVector();
				final double rotationAngle = Math.atan2(rotationVec.z, rotationVec.x);

				final BiPredicate<BlockPos, BlockState> blockDamagePredicate = getBlockDamagePredicate(world, true, user);

				final BlockPos[] positions = findAdjacentBlocksFromViewAngle(BlockPos.ofFloored(user.getPos().add(rotationVec.getHorizontal().normalize().multiply(0.5f))), rotationAngle);
				for (int y: new int[] {0, 1}) {
					for (BlockPos position : positions) {
						final BlockPos pos = position.up(y);
						if (blockDamagePredicate.test(pos, world.getBlockState(pos))) {
							world.setBlockState(pos, Blocks.HAY_BLOCK.getDefaultState());
						}
					}
				}

				user.getStackInHand(hand).damage(5, user, hand);

				return ActionResult.SUCCESS;
			})
			.simpleTooltip(Text.translatable("tooltip.wheatenator")),
			ItemGroups.COMBAT
	);
	Item DEATH_RAIN = registerAndGroup("death_rain", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(3)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				rainPositions(world, user.getPos(), 15, 15f, 20f, 35f, pos -> {
					final TntEntity tnt = new TntEntity(world, pos.x, pos.y, pos.z, user);

					tnt.setFuse(world.random.nextBetween(45, 75));
					world.spawnEntity(tnt);
				});

				user.getStackInHand(hand).damage(1, user, hand);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item ALPHA_BOW = registerAndGroup("alpha_bow", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(256)
			.enchantable(2)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				final ItemStack itemStack = user.getStackInHand(hand);

				if (world instanceof ServerWorld serverWorld) {
					final ArrowEntity arrow = new ArrowEntity(world, user, Items.AIR.getDefaultStack(), itemStack);
					arrow.setPosition(user.getEyePos());
					arrow.setVelocity(user.getRotationVector().multiply(1.75f));
					arrow.setPitch(-user.getPitch());
					arrow.setYaw(-user.getYaw());

					ProjectileEntity.spawn(arrow, serverWorld, itemStack);
				}

				playBowSound(world, user, 1f);

				itemStack.damage(1, user, hand);

				return ActionResult.FAIL;
			}),
			ItemGroups.COMBAT
	);
	Item SNIPER_RIFLE = registerAndGroup("sniper_rifle", SocWarsItem::new, new ExtendedItemSettings()
			.maxDamage(10)
			.useCooldown(2f)
			.rarity(Rarity.EPIC)
			.itemUse((world, user, hand) -> {
				final ItemStack itemStack = user.getStackInHand(hand);
				itemStack.damage(1, user, hand);
				world.playSound(null, user.getX(), user.getY(), user.getZ(), Sounds.SNIPER_RIFLE_SHOT, SoundCategory.PLAYERS);

				//block damage and particles
				final Set<Vec3d> particlePositions = new HashSet<>();
				final BiPredicate<BlockPos, BlockState> damage = getBlockDamagePredicate(world, true, user);

				//actually technical stuff setup
				final double maxDistance = 30d;
				final double width = 1.5f;
				final double boxStep = 8d;

				final Vec3d eyePos = user.getEyePos();
				final Vec3d direction = user.getRotationVector();

				for (double distance = 0; distance < maxDistance;) {
					final Box checkBox = new Box(eyePos.add(direction.multiply(distance)), eyePos.add(direction.multiply(distance += boxStep))).expand(Math.ceil(width));

					iterateInCube(new IntBox(checkBox), pos -> {
						if (isPointWithinDistanceOfUnitVector(eyePos, direction, pos.toCenterPos(), width) && !world.isAir(pos)) {
							if (damage.test(pos, world.getBlockState(pos))) world.setBlockState(pos, Blocks.AIR.getDefaultState());
							if (world.random.nextFloat() < 0.3f) particlePositions.add(pos.toCenterPos());
						}
					});
				}

				if (world instanceof ServerWorld serverWorld) {
					final Collection<ServerPlayerEntity> nearbyPlayers = PlayerLookup.around(serverWorld, eyePos, 250d);

					//particles 2: electric boogaloo
					final Vec3d velocity = direction.multiply(-0.05d).add(0d, 0.025d, 0d);
					final Vec3d mainParticePosition = eyePos.add(direction);
					nearbyPlayers.forEach(player -> {
						ServerPlayNetworking.send(player, new BatchParticlePayload(ParticleTypes.LARGE_SMOKE, particlePositions.stream().toList(), velocity));
						player.networkHandler.sendPacket(new ParticleS2CPacket(ParticleTypes.FLAME, true, true, mainParticePosition.x, mainParticePosition.y, mainParticePosition.z, 0.07f, 0.07f, 0.07f, 0.1f, 16));
					});

					//entity damage
					final List<Entity> entities = new ArrayList<>();
					EntityHitResult hitResult;
					do {
						hitResult = ProjectileUtil.raycast(user, eyePos, eyePos.add(direction.multiply(maxDistance)), new Box(eyePos, eyePos.add(direction.multiply(maxDistance))), entity -> !entities.contains(entity), maxDistance * maxDistance);
						if (hitResult != null) {
							entities.add(hitResult.getEntity());
						}
					} while (hitResult != null && hitResult.getEntity().squaredDistanceTo(user.getPos()) <= maxDistance * maxDistance);

					entities.forEach(entity -> entity.damage(serverWorld, damageSource(serverWorld, DamageTypes.SNIPER_RIFLE, user), 18f));
				}

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item RED_SHELL = registerAndGroup("red_shell", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(5f)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				if (world.isClient) return null;

				world.spawnEntity(new RedShellEntity(world, user.getPos(), user));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			})
			.simpleTooltip(Text.translatable("tooltip.red_shell")),
			ItemGroups.COMBAT
	);
	Item BLUE_SHELL = registerAndGroup("blue_shell", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(5f)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				if (world.isClient) return null;

				world.spawnEntity(new BlueShellEntity(world, user.getPos(), user));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item TAUNT_STICK = registerAndGroup("taunt_stick", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.useCooldown(0.75f)
			.itemUse((world, user, hand) -> {
				final SoundEvent soundEvent = TAUNTS[world.random.nextBetween(0, TAUNTS.length - 1)];
				world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.MASTER);

				return ActionResult.SUCCESS;
			})
	);
	Item BIOHACKER = registerAndGroup("biohacker", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.POTIONWEAPON, 2.5f, -2.2f)
			.rarity(Rarity.RARE)
			.maxDamage(300)
			.itemHit((stack, target, attacker) -> {
				if (target.getRandom().nextFloat() < 0.25f) target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 3 * 20, 1, false, true));
			}),
			ItemGroups.COMBAT
	);
	Item GOO_SHOVEL = registerAndGroup("goo_shovel", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.POTIONWEAPON, -2f, -3f)
			.component(DataComponentTypes.TOOL, new ToolComponent(List.of(ToolComponent.Rule.ofNeverDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)), ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE), 6.5f)), 1.0f, 1, true))
			.rarity(Rarity.UNCOMMON) //The code above is definitely okay and not terrible whatsoever //TODO: Make some custom method in ExtendedItemSettings to do this for me in an "elegant" way
			.maxDamage(20)
			.itemHit((stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 1, false, true));
			}),
			ItemGroups.COMBAT
	);
	Item STATUE_BLADE = registerAndGroup("statue_blade", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.POTIONWEAPON, 3.5f, -2.3f)
			.rarity(Rarity.RARE)
			.maxDamage(300)
			.itemHit((stack, target, attacker) -> {
				final Consumer<LivingEntity> effectApplicator = entity -> { //TODO: Extract this to a lib file because this is gross
					entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 6, false, true));
					//entity.addStatusEffect(new StatusEffectInstance(ModEffects.NO_JUMPING, 3 * 20, 6, false, true)); //TODO: Make an effect that prevents jumping
				};
				effectApplicator.accept(target);
				effectApplicator.accept(attacker);
			}),
			ItemGroups.COMBAT
	);
	Item ALCHEMISM = registerAndGroup("alchemism", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.POTIONWEAPON, 4f, -2.5f)
			.rarity(Rarity.EPIC)
			.maxDamage(500)
			.itemHit((stack, target, attacker) -> {
				for (EffectRecord effect : ALCHEMISM_EFFECTS) {
					target.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier(), false, true));
					if (effect.effect().value().isBeneficial()) {
						attacker.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier(), false, true));
					}
				}
			}),
			ItemGroups.COMBAT
	);
	Item RED_SWORD = registerAndGroup("red_sword", SocWarsItem::new, new ExtendedItemSettings()
			.itemHit((stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 0, false, true));
			})
			.sword(ToolMaterials.POTIONWEAPON, 1f, -2.1f)
			.maxDamage(400),
			ItemGroups.COMBAT
	);
	Item PURPLE_SWORD = registerAndGroup("purple_sword", SocWarsItem::new, new ExtendedItemSettings()
			.itemHit((stack, target, attacker) -> {
				attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1 * 20, 0, false, true));
			})
			.sword(ToolMaterials.POTIONWEAPON, 1f, -2.2f)
			.rarity(Rarity.RARE)
			.maxDamage(900),
			ItemGroups.COMBAT
	);
	Item GREEN_SWORD = registerAndGroup("green_sword", SocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, 7f, -2.15f)
			.rarity(Rarity.UNCOMMON)
			.maxDamage(700)
			.attributeModifiers(new AttributeModifiersComponent(List.of(
					new AttributeModifiersComponent.Entry(EntityAttributes.JUMP_STRENGTH, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "green_sword_jump"), 0.2d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
					new AttributeModifiersComponent.Entry(EntityAttributes.SAFE_FALL_DISTANCE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "green_sword_fall_distance"), 2d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
			))),
			ItemGroups.COMBAT
	);
	Item DICE_OF_FATE = registerAndGroup("dice_of_fate", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				if (world.isClient) return ActionResult.SUCCESS;

				final int effect = world.random.nextBetween(0, 5);

				user.getStackInHand(hand).decrementUnlessCreative(1, user);
				user.sendMessage(Text.translatable("dice_of_fate.roll_result", effect + 1), true);

				switch (effect) {
					case 0 -> giveItem(user, new ItemStack(NETHERITE_SWORD), hand);
					case 1 -> giveItem(user, new ItemStack(ENCHANTED_GOLDEN_APPLE), hand);
					case 2 -> {
						if (world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES)) {
							user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1, 1, false, false, false));
							user.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1, 11, false, false, false));
							world.createExplosion(null, Explosion.createDamageSource(world, null), null, user.getX(), user.getY(), user.getZ(), 8.0f, false, World.ExplosionSourceType.TNT);
						}
					}
					case 3 -> world.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOptional(ModItemTags.RINGS).flatMap(entries -> entries.getRandom(world.random)).ifPresent(ring -> giveItem(user, new ItemStack(ring), hand));
					case 4 -> ServerPlayNetworking.send((ServerPlayerEntity)user, new AddVelocityPayload(new Vec3d(0d, 2.5d, 0d)));
					case 5 -> {
						for (EffectRecord effectRecord : GARBO_EFFECTS) {
							user.addStatusEffect(new StatusEffectInstance(effectRecord.effect(), 20 * 20, effectRecord.amplifier(), false, true, true));
						}
					}
				}

				return ActionResult.FAIL;
			})
	);
	Item SHRINKING_PILLS = registerAndGroup("shrinking_pills", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.conditionallyConsumable(0.8f, (stack, world, user) -> {
				scaleEntity(user, SQRT2 * 0.5f);
			}, user -> {
				return canMultiplyAttribute(user, EntityAttributes.SCALE, SCALE_MODIFIER_ID, SQRT2 * 0.5f, MAX_SCALE_FACTOR);
			})
			.simpleTooltip(Text.translatable("tooltip.shrinking_pills")),
			ItemGroups.FOOD_AND_DRINK
	);
	Item BIGGENING_PILLS = registerAndGroup("biggening_pills", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.conditionallyConsumable(0.8f, (stack, world, user) -> {
				scaleEntity(user, SQRT2);
			}, user -> {
				return canMultiplyAttribute(user, EntityAttributes.SCALE, SCALE_MODIFIER_ID, SQRT2, MAX_SCALE_FACTOR);
			})
			.simpleTooltip(Text.translatable("tooltip.biggening_pills")),
			ItemGroups.FOOD_AND_DRINK
	);
	Item CHORUS_FRUIT_SALAD = registerAndGroup("chorus_fruit_salad", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(5f)
			.consumable(2.4f, (stack, world, user) -> {
				if (world.isClient) return;

				final boolean success = randomTeleport(world, user, 75, 50, 15f);
				if (!success && user instanceof PlayerEntity player) player.sendMessage(Text.translatable("chorus_fruit_salad.fail"), false);
			})
			.rarity(Rarity.RARE),
			ItemGroups.FOOD_AND_DRINK
	);
	Item FIREBALL = registerAndGroup("fireball", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.75f)
			.throwable(BWFireballEntity::fireball, 1.75f),
			ItemGroups.COMBAT
	);
	Item WATERBALL = registerAndGroup("waterball", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.75f)
			.throwable(BWFireballEntity::waterball, 1.75f),
			ItemGroups.COMBAT
	);
	Item SNAIL_FIREBALL = registerAndGroup("snail_fireball", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.75f)
			.rarity(Rarity.EPIC)
			.throwable(BWFireballEntity::snail, 0.175f)
			.simpleTooltip(Text.translatable("tooltip.snail_fireball").withColor(0xe6e475)),
			ItemGroups.COMBAT
	);
	Item LIGHTNING_ORB = registerAndGroup("lightning_orb", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.75f)
			.throwable(BWFireballEntity::lightningOrb, 2f)
			.rarity(Rarity.UNCOMMON),
			ItemGroups.COMBAT
	);
	Item DRAGON_FIREBALL = registerAndGroup("dragon_fireball", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.75f)
			.throwable(BWFireballEntity::dragonFireball, 1.5f)
			.dynamicTooltip((stack, time) -> List.of(Text.translatable("tooltip.dragon_fireball").withColor(Color.HSBtoRGB(time / 50f, 1f, 1f))))
			.rarity(Rarity.UNCOMMON),
			ItemGroups.COMBAT
	);
	Item THROWABLE_TNT = registerAndGroup("throwable_tnt", SocWarsItem::new, new ExtendedItemSettings()
			.throwable(BWFireballEntity::throwableTnt, 0.6f)
			.useCooldown(0.75f)
	);
	Item ENDER_BEAM = registerAndGroup("ender_beam", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.75f)
			.throwable(EnderBeamEntity::new, 2.5f),
			ItemGroups.TOOLS
	);
	Item HAND_GRENADE = registerAndGroup("hand_grenade", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(1f)
			.rarity(Rarity.UNCOMMON)
			.throwable(HandGrenadeEntity::new, 0.8f)
			.simpleTooltip(Text.translatable("tooltip.hand_grenade")),
			ItemGroups.COMBAT
	);
	Item HOLY_HAND_GRENADE = registerAndGroup("holy_hand_grenade", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(3f)
			.rarity(Rarity.RARE)
			.throwable(HolyHandGrenadeEntity::new, 0.65f)
			.simpleIterTooltip(10, i -> Text.translatable("tooltip.holy_hand_grenade." + i)),
			ItemGroups.COMBAT
	);
	Item MOLOTOV_COCKTAIL = registerAndGroup("molotov_cocktail", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.5f)
			.throwable(MolotovCocktailEntity::new, 0.7f)
			.rarity(Rarity.UNCOMMON),
			ItemGroups.COMBAT
	);
	Item POCKET_SAND = registerAndGroup("pocket_sand", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(0.5f)
			.throwable(PocketSandEntity::new, 0.9f)
			.simpleTooltip(Text.translatable("tooltip.pocket_sand")),
			ItemGroups.COMBAT
	);
	Item EXPON = registerAndGroup("expon", TickingSocWarsItem::new, new ExtendedItemSettings()
			.sword(ToolMaterials.BASE, -0.5f, -1.6f)
			.maxDamage(300)
			.component(ModItemComponentsReworked.EXPON, new ExponComponent())
			.rarity(Rarity.UNCOMMON)
			.simpleTooltip(Text.translatable("tooltip.expon")),
			ItemGroups.COMBAT
	);
	Item EXTENDO_BRIDGE = registerAndGroup("extendo_bridge", SocWarsItem::new, new ExtendedItemSettings()
			.rarity(Rarity.UNCOMMON)
			.useCooldown(0.25f)
			.itemUse((world, user, hand) -> {
				final BlockState block = BLOCKS[world.random.nextBetween(0, BLOCKS.length - 1)].getDefaultState();

				float lengthMarched = 0f;

				final Vec3d direction = user.getRotationVector().multiply(1f, 0f, 1f).normalize();

				Vec3d currentPosition = new Vec3d(user.getX() - 0.5f, Math.floor(user.getY() - 0.75f), user.getZ() - 0.5f);

				final float stepSize = 0.1f;

				while (lengthMarched < 16f) {
					final BlockPos pos = new BlockPos((int)currentPosition.x, (int)currentPosition.y, (int)currentPosition.z);
					if (world.getBlockState(pos) == Blocks.AIR.getDefaultState()) {
						world.setBlockState(pos, block);
					}

					currentPosition = currentPosition.add(direction.multiply(stepSize));
					lengthMarched += stepSize;
				}

				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item SHARPENED_POKING_STICK = registerAndGroup("sharpened_poking_stick", Item::new, new ExtendedItemSettings()
			.maxDamage(150)
			.rarity(Rarity.UNCOMMON)
			.attributeModifiers(new AttributeModifiersComponent(List.of(
					new AttributeModifiersComponent.Entry(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_reach"), 1.75, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND),
					new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_damage"), 5, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND),
					new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_attack_speed"), 0.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
			))),
			ItemGroups.COMBAT
	);
	Item BALUSTRADE = registerAndGroup("balustrade", Item::new, new ExtendedItemSettings());
	Item PORTABLE_STEPPING_STOOL = registerAndGroup("portable_stepping_stool", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.DOUBLE_JUMP, true)
			.simpleTooltip(Text.translatable("tooltip.portable_stepping_stool"))
	);
	Item JET_SHOPPING_TROLLEY = registerAndGroup("jet_shopping_trolley", SocWarsItem::new, new ExtendedItemSettings()
			.maxCount(1)
			.rarity(Rarity.RARE)
			.itemUse((world, user, hand) -> {
				user.getStackInHand(hand).decrementUnlessCreative(1, user);
				world.spawnEntity(new JetShoppingTrolleyEntity(user));

				return ActionResult.SUCCESS;
			})
	);
	Item PARKOUR_RESET = registerAndGroup("parkour_reset", SocWarsItem::new, new ExtendedItemSettings()
			.component(ModItemComponentsReworked.COMMAND_FUNCTION, new CommandFunctionComponent("parkour:cancel", "parkour:checkpoint"))
	);
	Item GRAVITY_ORB = registerAndGroup("gravity_orb", SocWarsItem::new, new ExtendedItemSettings()
			.useCooldown(7.5f)
			.rarity(Rarity.UNCOMMON)
			.itemHit((stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(ModEffects.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));
				attacker.getStackInHand(Hand.MAIN_HAND).decrementUnlessCreative(1, attacker);
			})
			.itemUse((world, user, hand) -> {
				user.addStatusEffect(new StatusEffectInstance(ModEffects.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item STONE_SHEARS = registerAndGroup("stone_shears", Item::new, new ExtendedItemSettings()
			.shears(ToolMaterial.STONE),
			ItemGroups.TOOLS
	);
	Item DIAMOND_SHEARS = registerAndGroup("diamond_shears", Item::new, new ExtendedItemSettings()
			.shears(ToolMaterial.DIAMOND),
			ItemGroups.TOOLS
	);
	Item EMERALD_PICKAXE = registerAndGroup("emerald_pickaxe", Item::new, new ExtendedItemSettings()
			.pickaxe(ToolMaterials.EMERALD, 1f, -2.8f),
			ItemGroups.TOOLS
	);
	Item EMERALD_AXE = registerAndGroup("emerald_axe", Item::new, new ExtendedItemSettings()
			.axe(ToolMaterials.EMERALD, 5f, -3f),
			ItemGroups.TOOLS
	);
	Item UNOBTANIUM_PICKAXE = registerAndGroup("unobtanium_pickaxe", Item::new, new ExtendedItemSettings()
			.pickaxe(ToolMaterials.UNOBTANIUM, 1f, -2.8f),
			ItemGroups.TOOLS
	);
	Item UNOBTANIUM_AXE = registerAndGroup("unobtanium_axe", Item::new, new ExtendedItemSettings()
			.axe(ToolMaterials.UNOBTANIUM, 5f, -3f),
			ItemGroups.TOOLS
	);
	Item UNOBTANIUM_SHEARS = registerAndGroup("unobtanium_shears", Item::new, new ExtendedItemSettings()
			.shears(ToolMaterials.UNOBTANIUM),
			ItemGroups.TOOLS
	);
	Item BEDROCK_PICKAXE = registerAndGroup("bedrock_pickaxe", Item::new, new ExtendedItemSettings()
			.pickaxe(ToolMaterials.BEDROCK, 1f, -2.8f)
			.rarity(Rarity.UNCOMMON),
			ItemGroups.TOOLS
	);
	Item BEDROCK_AXE = registerAndGroup("bedrock_axe", Item::new, new ExtendedItemSettings()
			.axe(ToolMaterials.BEDROCK, 5f, -3f)
			.rarity(Rarity.UNCOMMON),
			ItemGroups.TOOLS
	);
	Item THE_PICKAXE = registerAndGroup("the_pickaxe", Item::new, new ExtendedItemSettings()
			.pickaxe(ToolMaterials.FISH, 1f, -2.8f)
			.rarity(Rarity.RARE),
			ItemGroups.TOOLS
	);
	Item THE_AXE = registerAndGroup("the_axe", Item::new, new ExtendedItemSettings()
			.axe(ToolMaterials.FISH, 5f, -3f)
			.rarity(Rarity.RARE),
			ItemGroups.TOOLS
	);
	Item THE_SHEARS = registerAndGroup("the_shears", Item::new, new ExtendedItemSettings()
			.shears(ToolMaterials.FISH)
			.rarity(Rarity.RARE),
			ItemGroups.TOOLS
	);
	Item FISHAXE = registerAndGroup("fishaxe", Item::new, new ExtendedItemSettings()
			.component(DataComponentTypes.TOOL, new ToolComponent(List.of(
					ToolComponent.Rule.ofNeverDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)),
					ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.PICKAXE_MINEABLE), ToolMaterials.FISH.speed()),
					ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.AXE_MINEABLE), ToolMaterials.FISH.speed()),
					ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE), ToolMaterials.FISH.speed()),
					ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.HOE_MINEABLE), ToolMaterials.FISH.speed()) //TODO: Shears Mineable tag?
			), ToolMaterials.FISH.speed(), 1, true))
			.component(DataComponentTypes.WEAPON, new WeaponComponent(1, 2.5f))
			.enchantable(50)
			.maxDamage(ToolMaterials.FISH.durability() * 3/2)
			.rarity(Rarity.EPIC),
			ItemGroups.TOOLS
	);
	Item MORPH_WAND = ModItemsReworked.register("morph_wand", SocWarsItem::new, new ExtendedItemSettings()
			.itemUse((world, user, hand) -> {
				if (user instanceof ServerPlayerEntity serverPlayer && user.isSneaking()) {
					Morph.clearMorph(serverPlayer);
					return ActionResult.SUCCESS_SERVER;
				}
				return ActionResult.PASS;
			})
			.itemUseOnBlock((world, user, hand, state, pos) -> {
				if (user instanceof ServerPlayerEntity serverPlayer) {
					if (user.isSneaking()) {
						Morph.clearMorph(serverPlayer);
					} else {
						final boolean acceptedMorph = Morph.attemptMorph(serverPlayer, state);
						if (!acceptedMorph) return ActionResult.FAIL;

						user.getItemCooldownManager().set(user.getStackInHand(hand), 15 * 20);
					}
					return ActionResult.SUCCESS_SERVER;
				}
				return ActionResult.PASS;
			})
			.itemHitAll((player, stack) -> {
				if (player instanceof ServerPlayerEntity serverPlayer) Morph.rotateMorph(serverPlayer, serverPlayer.isSneaking());
			})
	);




	Item DEPOSITABLE_CHEST = registerAndGroup("depositable_chest", DepositableChestBlockItem::new, new ExtendedItemSettings(),
			ModItemGroups.MAP_MAKING_TOOLS_KEY
	);
	Item DEPOSITABLE_ENDER_CHEST = registerAndGroup("depositable_ender_chest", DepositableEnderChestBlockItem::new, new ExtendedItemSettings(),
			ModItemGroups.MAP_MAKING_TOOLS_KEY
	);
	List<Item> CHEST_PLACEHOLDERS = mapFromRange(0, 4, i -> registerAndGroup("chest_placeholder_tier_" + i, settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, i), new ExtendedItemSettings(),
			ModItemGroups.MAP_MAKING_TOOLS_KEY)
	);
	Map<DyeColourWithEmpty, Item> MANY_BEDS_BLOCKS = Arrays.stream(DyeColourWithEmpty.values()).filter(DyeColourWithEmpty::isPresent).collect(Collectors.toMap(Function.identity(), colour -> registerAndGroup("many_beds_block_" + colour.asString(), settings -> new ColourStateBlockItem(MANY_BEDS_BLOCK, settings, colour), new ExtendedItemSettings(),
			ModItemGroups.BLOCKS_KEY))
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

	@SuppressWarnings("SameParameterValue")
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

	private static void giveItem(PlayerEntity user, ItemStack item, Hand hand) {
		if (user.getStackInHand(hand).isEmpty()) {
			user.setStackInHand(hand, item);
		} else {
			user.giveItemStack(item);
		}
	}
	//endregion
}
