package com.soc.items;

import com.soc.events.ModEvents;
import com.soc.items.util.ModItemGroups;
import com.soc.itemsrework.ModItemsReworked;
import com.soc.itemsrework.SocWarsItem;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import com.soc.player.PlayerDataManager;
import com.soc.util.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.soc.items.util.ModItemGroups.addItemToGroupsAndBaseItemGroup;

public class MorphWand extends SocWarsItem {
	public static void initialise() {
		addItemToGroupsAndBaseItemGroup(MORPH_WAND, ModItemGroups.ITEMS_KEY);
	}

	public MorphWand(Settings settings) {
		super(settings.component(ModItemComponentsReworked.CANCEL_BLOCK_INTERACTION, Unit.INSTANCE));
	}

	public static final Item MORPH_WAND = ModItemsReworked.register("morph_wand", MorphWand::new, new Settings());

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
			return use(context.getWorld(), serverPlayer, context.getHand(), context.getWorld().getBlockState(context.getBlockPos()));
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (user instanceof ServerPlayerEntity serverPlayer) {
			return use(world, serverPlayer, hand, null);
		} else {
			return ActionResult.FAIL;
		}
	}

	@Override
	public void onAttackButtonPressed(PlayerEntity player, ItemStack stack) {
		if (player instanceof ServerPlayerEntity serverPlayer) rotateMorph(serverPlayer, serverPlayer.isSneaking());
	}

	private static ActionResult use(World world, ServerPlayerEntity player, Hand hand, @Nullable BlockState blockState) {
		if (player.isSneaking()) {
			return clearMorph(player);
		} else if (blockState != null) {
			final ActionResult result = attemptMorph(player, blockState);
			if (result.isAccepted()) player.getItemCooldownManager().set(player.getStackInHand(hand), 15 * 20);
			return result;
		}
		return ActionResult.FAIL;
	}

	private static ActionResult attemptMorph(ServerPlayerEntity player, BlockState blockState) {
		if (blockState.isIn(ModBlockTags.DISALLOW_MORPH)) {
			player.sendMessage(Text.literal("Nice try ;)"));
			return ActionResult.FAIL;
		}

		final boolean allowMorph = ModEvents.ON_PLAYER_MORPHED.invoker().onPlayerMorphed(player, blockState);
		if (allowMorph) {
			PlayerDataManager.getPlayerData(player).setMorph(blockState, player);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	public static ActionResult clearMorph(ServerPlayerEntity serverPlayer) {
		PlayerDataManager.getPlayerData(serverPlayer).setMorph(null, serverPlayer);
		return ActionResult.SUCCESS;
	}

	private static void rotateMorph(ServerPlayerEntity serverPlayer, boolean reverse) {
		PlayerDataManager.getPlayerData(serverPlayer).rotateMorph(serverPlayer, reverse);
	}
}
