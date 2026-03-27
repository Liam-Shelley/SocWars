package com.soc.items;

import com.soc.game.manager.GamesManager;
import com.soc.game.manager.HideAndSeekGameManager;
import com.soc.items.util.EffectRecord;
import com.soc.items.util.ModItems;
import com.soc.materials.ToolMaterials;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class SeekingStick extends Item {
    public SeekingStick(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(SEEKING_STICK, ItemGroups.COMBAT);
    }

    public static final Item SEEKING_STICK = ModItems.register("seeking_stick", SeekingStick::new, new Settings().rarity(Rarity.UNCOMMON));

    @Override
    public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        GamesManager.getInstance().getGame(target).ifPresent(game -> {
            if (game instanceof HideAndSeekGameManager hideAndSeekGameManager) {
                hideAndSeekGameManager.findPlayer(attacker, (ServerPlayerEntity)target);
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.seeking_stick"));
    }
}
