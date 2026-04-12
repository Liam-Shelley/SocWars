package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.soc.lib.SocWarsLib.ifNotNull;
import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class GeneratorUpgradeShopItem extends TieredShopItem<GeneratorUpgradeShopItem> implements TooltipProvider {
    public static final int ID = 9;
    private static final PacketCodec<RegistryByteBuf, GeneratorUpgradeShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(ItemStack.PACKET_CODEC), GeneratorUpgradeShopItem::getOptionalIcon, PacketCodecs.collection(ArrayList::new, Cost.PACKET_CODEC), GeneratorUpgradeShopItem::getCosts, PacketCodecs.INTEGER, GeneratorUpgradeShopItem::getTier, GeneratorUpgradeShopItem::new);

    private final ItemStack icon;

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    private GeneratorUpgradeShopItem(ItemStack icon, List<Cost> costs, int tier) {
        super(costs, tier);
        this.icon = icon;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public GeneratorUpgradeShopItem(Optional<ItemStack> icon, List<Cost> costs, Integer tier) {
        this(icon.orElse(ItemStack.EMPTY), costs, tier);
    }

    public GeneratorUpgradeShopItem(JsonObject object) {
        this(
                getDefaultedItem(object, ICON_KEY, Items.BARRIER.getDefaultStack()),
                getDefaultedObjectList(object, COSTS_KEY, Cost::new, Optional.of(Cost.ERROR_SIGNAL)),
                0
        );
    }

    public GeneratorUpgradeShopItem(Reader reader) {
        this(deserialize(reader));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (!super.buy(player, context)) return false;

        if (player instanceof ServerPlayerEntity serverPlayer) return context.getManager().buyGeneratorUpgrade(serverPlayer, this.tier);

        return true;
    }

    @Override
    protected MutableText getBaseName() {
        return Text.translatable("upgrade.generator");
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public PacketCodec<RegistryByteBuf, GeneratorUpgradeShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public GeneratorUpgradeShopItem lazyClone() {
        return new GeneratorUpgradeShopItem(this.icon.copy(), this.costs, 0);
    }

    @Override
    @Nullable
    public Text getTooltip() {
        return TooltipProvider.getEnchantmentTooltip(this.getIcon().get(DataComponentTypes.ENCHANTMENTS));
    }

    @Override
    public Text getDisplayName() {
        final Text oldLevel = Text.translatable("upgrade.generator.tier." + this.tier).append(" ").append(this.getBaseName());
        final Text newLevel = Text.translatable("upgrade.generator.tier." + (this.tier + 1)).append(" ").append(this.getBaseName());
        return this.tier == this.costs.size() ? oldLevel : Text.translatable("hud.a_to_b", oldLevel, newLevel).formatted(Formatting.AQUA);
    }
}
