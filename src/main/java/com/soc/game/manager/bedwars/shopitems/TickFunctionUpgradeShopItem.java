package com.soc.game.manager.bedwars.shopitems;

import com.google.gson.JsonObject;
import com.soc.game.manager.bedwars.tickfunctions.AbstractTickFunction;
import com.soc.game.manager.bedwars.tickfunctions.TickFunctions;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.soc.lib.json.JsonHelper.*;
import static net.minecraft.util.JsonHelper.deserialize;

public class TickFunctionUpgradeShopItem extends TieredShopItem<TickFunctionUpgradeShopItem> {
    public static final int ID = 7;
    private static final PacketCodec<RegistryByteBuf, TickFunctionUpgradeShopItem> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, Cost.PACKET_CODEC), TickFunctionUpgradeShopItem::getCosts, Identifier.PACKET_CODEC, TickFunctionUpgradeShopItem::getFunctionId, PacketCodecs.INTEGER, TickFunctionUpgradeShopItem::getTier, TickFunctionUpgradeShopItem::new);

    public static void initialise() {
        ShopItem.DECODER_MAP.put(ID, PACKET_CODEC::decode);
    }

    private final AbstractTickFunction tickFunction;
    private final ItemStack icon;

    public TickFunctionUpgradeShopItem(List<Cost> costs, AbstractTickFunction tickFunction, int tier) {
        super(costs, tier);
        this.tickFunction = tickFunction;
        this.icon = tickFunction.getIcon().copy();
    }

    public TickFunctionUpgradeShopItem(List<Cost> costs, Identifier id, int tier) {
        super(costs, tier);
        this.tickFunction = TickFunctions.getOrThrow(id);
        this.icon = tickFunction.getIcon().copy();
    }

    public TickFunctionUpgradeShopItem(JsonObject object) {
        this(
                getDefaultedObjectList(object, COSTS_KEY, Cost::new, Optional.of(Cost.ERROR_SIGNAL)),
                getDefaultedTickFunction(object, AbstractTickFunction.KEY),
                0
        );
    }

    public TickFunctionUpgradeShopItem(Reader reader) {
        this(deserialize(reader));
    }

    @Override
    public boolean buy(PlayerEntity player, AbstractShopScreenHandler context) {
        if (!super.buy(player, context)) return false;

        if (player instanceof ServerPlayerEntity serverPlayer) context.getManager().buyTickFunctionUpgrade(serverPlayer, this.tickFunction, this.tier);

        return true;
    }

    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public PacketCodec<RegistryByteBuf, TickFunctionUpgradeShopItem> getPacketCodec() {
        return PACKET_CODEC;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public TickFunctionUpgradeShopItem lazyClone() {
        return new TickFunctionUpgradeShopItem(this.costs, this.tickFunction, this.tier);
    }

    public Identifier getFunctionId() {
        return this.tickFunction.getId();
    }

    @Override
    protected MutableText getBaseName() {
        return this.tickFunction.getName();
    }
}
