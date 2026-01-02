package com.soc.game.manager.bedwars;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.soc.game.map.DyeColourWithEmpty;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.io.Reader;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static com.soc.lib.json.JsonHelper.getDefaultedItem;
import static com.soc.lib.json.JsonHelper.getDefaultedObject;
import static net.minecraft.util.JsonHelper.deserialize;

public class TeamShopItem implements ShopItem<TeamShopItem> {
    public static final int ID = 3;
    private static final PacketCodec<RegistryByteBuf, TeamShopItem> PACKET_CODEC = PacketCodec.tuple(Cost.PACKET_CODEC, TeamShopItem::getCost, PacketCodecs.optional(ItemStack.PACKET_CODEC), TeamShopItem::getOptionalStack, TeamShopItem::new);

    static {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    public static final TeamShopItem EMPTY = new TeamShopItem(Cost.DEFAULT, ItemStack.EMPTY);

    private final Cost cost;
    private Either<ItemStack, Map<DyeColourWithEmpty, ItemStack>> stack;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public TeamShopItem(Cost cost, Optional<ItemStack> stack) {
        this.cost = cost;
        this.stack = Either.left(stack.orElse(Items.RESIN_BLOCK.getDefaultStack()));
    }

    public TeamShopItem(Cost cost, Map<DyeColourWithEmpty, ItemStack> stackMap) {
        this.cost = cost;
        this.stack = Either.right(stackMap);
    }

    public TeamShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object)
        );
    }

    public TeamShopItem(Reader reader) {
        this(
                deserialize(reader)
        );
    }

    @Override
    public boolean buy(PlayerEntity player, BedwarsShopScreenHandler context) {
        final boolean gaveStack = this.giveStack(this.stack, player, OptionalInt.empty());
        if (gaveStack) this.takeItems(player);

        return gaveStack;
    }

    private Optional<ItemStack> getOptionalStack() {
        return this.stack.isEmpty() ? Optional.empty() : Optional.of(this.stack);
    }

    @Override
    public ItemStack getIcon() {
        return this.stack;
    }

    @Override
    public Cost getCost() {
        return this.cost;
    }

    @Override
    public PacketCodec<RegistryByteBuf, TeamShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public TeamShopItem lazyClone() {
        return this;
    }
}
