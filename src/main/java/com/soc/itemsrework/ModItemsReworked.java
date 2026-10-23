package com.soc.itemsrework;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.entities.BlueShellEntity;
import com.soc.entities.RedShellEntity;
import com.soc.game.manager.AbstractHidingGameManager;
import com.soc.game.manager.GamesManager;
import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModifyEquipmentFunction;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.IntBox;
import com.soc.materials.ToolMaterials;
import com.soc.mixin.GetAbsorptionAmountDataTracker;
import com.soc.networking.s2c.BatchParticlePayload;
import com.soc.util.DamageTypes;
import com.soc.util.ModBlockTags;
import com.soc.util.Sounds;
import com.soc.util.SphereExplosion;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.ToolComponent;
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
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.soc.game.manager.AbstractGameManager.getBlockDamagePredicate;
import static com.soc.items.BowItem.playBowSound;
import static com.soc.items.util.ModItemGroups.registerAndGroup;
import static com.soc.itemsrework.components.ModItemComponentsReworked.DAMAGE_TYPE;
import static com.soc.itemsrework.components.ModItemComponentsReworked.ITEM_HIT;
import static com.soc.lib.SocWarsLib.*;
import static com.soc.lib.SocWarsLib.isBlockHidden;
import static com.soc.util.SphereExplosion.fireExplosion;

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
	//endregion







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
	Item DASHREND = registerAndGroup("dashrend", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.DASH, 2.5f, -2f)
			.useCooldown(3.5f)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				dash(user, hand, 0.8f);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item VELOCITY_STAFF = registerAndGroup("velocity_staff", SocWarsItem::new, new Item.Settings()
			.maxDamage(300)
			.useCooldown(2f)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				dash(user, hand, 1.5f);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item VEXING_STAFF = registerAndGroup("vexing_staff", SocWarsItem::new, new Item.Settings()
			.maxDamage(5)
			.useCooldown(45f)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item YELLOW_SWORD = registerAndGroup("yellow_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 6f, -2.1f)
			.useCooldown(1.5f)
			.maxDamage(600)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item GOD_COMPLEX = registerAndGroup("god_complex", SocWarsItem::new, new Item.Settings()
			.useCooldown(5f)
			.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
			.rarity(Rarity.EPIC)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				user.addStatusEffect(new StatusEffectInstance(ModEffects.FLIGHT, 5 * 20, 0, false, false));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.god_complex"))),
			ItemGroups.TOOLS
	);
	Item SCROLL_OF_EAU = registerAndGroup("scroll_of_eau", SocWarsItem::new, new Item.Settings()
			.useCooldown(1f)
			.maxDamage(3)
			.rarity(Rarity.UNCOMMON)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				final @Nullable BlockPos hitPos = shootScroll(world, user, hand, pos -> {
					if (world.isAir(pos)) world.setBlockState(pos, Blocks.WATER.getDefaultState().with(Properties.LEVEL_15, 7));
				});
				ifNotNull(hitPos, pos -> world.setBlockState(pos.add(0,4,0), Blocks.WATER.getDefaultState()));

				return ActionResult.SUCCESS;
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.scroll_of_eau"))),
			ItemGroups.TOOLS
	);
	Item SCROLL_OF_HELLFIRE = registerAndGroup("scroll_of_hellfire", SocWarsItem::new, new Item.Settings()
			.useCooldown(1f)
			.maxDamage(3)
			.rarity(Rarity.UNCOMMON)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				shootScroll(world, user, hand, pos -> {
					if (world.isAir(pos)) world.setBlockState(pos, Blocks.LAVA.getDefaultState());
				});

				return ActionResult.SUCCESS;
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.scroll_of_hellfire"))),
			ItemGroups.TOOLS
	);
	Item C_U_E_B = registerAndGroup("c_u_e_b", SocWarsItem::new, new Item.Settings()
			.useCooldown(1f)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item SHRINK_RAY = registerAndGroup("shrink_ray", SocWarsItem::new, new Item.Settings()
			.maxDamage(16)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2 * 0.5f));

				return ActionResult.SUCCESS;
			})
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.shrink_ray"))),
			ItemGroups.TOOLS
	);
	Item BIGGENING_RAY = registerAndGroup("biggening_ray", SocWarsItem::new, new Item.Settings()
			.maxDamage(16)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				shootEntity(user, hand, 1, 10, 2 * 20, entity -> scaleEntity(entity, SQRT2));

				return ActionResult.SUCCESS;
			}),
			ItemGroups.TOOLS
	);
	Item THE_LINE = registerAndGroup("the_line", SocWarsItem::new, new Item.Settings()
			.maxCount(4)
			.useCooldown(2f)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item WHEATENATOR = registerAndGroup("wheatenator", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 4.5f, -2.4f)
			.maxDamage(350)
			.useCooldown(0.25f)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
			.component(DataComponentTypes.LORE, tooltip(Text.translatable("tooltip.wheatenator"))),
			ItemGroups.COMBAT
	);
	Item DEATH_RAIN = registerAndGroup("death_rain", SocWarsItem::new, new Item.Settings()
			.maxDamage(3)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item ALPHA_BOW = registerAndGroup("alpha_bow", SocWarsItem::new, new Item.Settings()
			.maxDamage(256)
			.enchantable(2)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item SNIPER_RIFLE = registerAndGroup("sniper_rifle", SocWarsItem::new, new Item.Settings()
			.maxDamage(10)
			.useCooldown(2f)
			.rarity(Rarity.EPIC)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
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
	Item RED_SHELL = registerAndGroup("red_shell", SocWarsItem::new, new Item.Settings()
			.useCooldown(5f)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				if (world.isClient) return null;

				world.spawnEntity(new RedShellEntity(world, user.getPos(), user));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item BLUE_SHELL = registerAndGroup("blue_shell", SocWarsItem::new, new Item.Settings()
			.useCooldown(5f)
			.rarity(Rarity.RARE)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				if (world.isClient) return null;

				world.spawnEntity(new BlueShellEntity(world, user.getPos(), user));
				user.getStackInHand(hand).decrementUnlessCreative(1, user);

				return ActionResult.SUCCESS;
			}),
			ItemGroups.COMBAT
	);
	Item TAUNT_STICK = registerAndGroup("taunt_stick", SocWarsItem::new, new Item.Settings()
			.rarity(Rarity.UNCOMMON)
			.useCooldown(0.75f)
			.component(ModItemComponentsReworked.ITEM_USE, (world, user, hand) -> {
				final SoundEvent soundEvent = TAUNTS[world.random.nextBetween(0, TAUNTS.length - 1)];
				world.playSound(null, user.getBlockPos(), soundEvent, SoundCategory.MASTER);

				return ActionResult.SUCCESS;
			})
	);
	Item BIOHACKER = registerAndGroup("biohacker", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.POTIONWEAPON, 2.5f, -2.2f)
			.rarity(Rarity.RARE)
			.maxDamage(300)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				if (target.getRandom().nextFloat() < 0.25f) target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 3 * 20, 1, false, true));
			}),
			ItemGroups.COMBAT
	);
	Item GOO_SHOVEL = registerAndGroup("goo_shovel", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.POTIONWEAPON, -2f, -3f)
			.component(DataComponentTypes.TOOL, new ToolComponent(List.of(ToolComponent.Rule.ofNeverDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)), ToolComponent.Rule.ofAlwaysDropping(Registries.createEntryLookup(Registries.BLOCK).getOrThrow(BlockTags.SHOVEL_MINEABLE), 6.5f)), 1.0f, 1, true))
			.rarity(Rarity.UNCOMMON) //The code above is definitely okay and not terrible whatsoever
			.maxDamage(20)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 5 * 20, 1, false, true));
			}),
			ItemGroups.COMBAT
	);
	Item STATUE_BLADE = registerAndGroup("statue_blade", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.POTIONWEAPON, 3.5f, -2.3f)
			.rarity(Rarity.RARE)
			.maxDamage(300)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				final Consumer<LivingEntity> effectApplicator = entity -> { //TODO: Extract this to a lib file because this is gross
					entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3 * 20, 6, false, true));
					//entity.addStatusEffect(new StatusEffectInstance(ModEffects.NO_JUMPING, 3 * 20, 6, false, true)); //TODO: Make an effect that prevents jumping
				};
				effectApplicator.accept(target);
				effectApplicator.accept(attacker);
			}),
			ItemGroups.COMBAT
	);
	Item ALCHEMISM = registerAndGroup("alchemism", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.POTIONWEAPON, 4f, -2.5f)
			.rarity(Rarity.EPIC)
			.maxDamage(500)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				for (EffectRecord effect : ALCHEMISM_EFFECTS) {
					target.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier(), false, true));
					if (effect.effect().value().isBeneficial()) {
						attacker.addStatusEffect(new StatusEffectInstance(effect.effect(), effect.duration(), effect.amplifier(), false, true));
					}
				}
			}),
			ItemGroups.COMBAT
	);
	Item RED_SWORD = registerAndGroup("red_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.POTIONWEAPON, 1f, -2.1f)
			.maxDamage(400)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 5 * 20, 0, false, true));
			}),
			ItemGroups.COMBAT
	);
	Item PURPLE_SWORD = registerAndGroup("purple_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.POTIONWEAPON, 1f, -2.2f)
			.rarity(Rarity.RARE)
			.maxDamage(900)
			.component(ITEM_HIT, (stack, target, attacker) -> {
				attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1 * 20, 0, false, true));
			}),
			ItemGroups.COMBAT
	);
	Item GREEN_SWORD = registerAndGroup("green_sword", SocWarsItem::new, new Item.Settings()
			.sword(ToolMaterials.BASE, 7f, -2.15f)
			.rarity(Rarity.UNCOMMON)
			.maxDamage(700)
			.attributeModifiers(new AttributeModifiersComponent(List.of(
					new AttributeModifiersComponent.Entry(EntityAttributes.JUMP_STRENGTH, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "green_sword_jump"), 0.2d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND),
					new AttributeModifiersComponent.Entry(EntityAttributes.SAFE_FALL_DISTANCE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "green_sword_fall_distance"), 2d, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND)
			))),
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





	//endregion
}
