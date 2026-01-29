package com.soc.items.util;

import com.soc.SocWars;
import com.soc.blocks.util.ModBlocks;
import com.soc.items.AttackFunctionWeapon;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface ItemGroups {
    RegistryKey<ItemGroup> ITEMS_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(SocWars.MOD_ID, "items_group"));
    ItemGroup ITEMS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AttackFunctionWeapon.DEVASTATOR_PRIME))
            .displayName(Text.translatable("item_group.socwars.items"))
            .build();

    RegistryKey<ItemGroup> BLOCKS_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(SocWars.MOD_ID, "blocks_group"));
    ItemGroup BLOCKS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.LIAM_BLOCK))
            .displayName(Text.translatable("item_group.socwars.blocks"))
            .build();

    RegistryKey<ItemGroup> MAP_MAKING_TOOLS_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(SocWars.MOD_ID, "map_making_tools_group"));
    ItemGroup MAP_MAKING_TOOLS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.TEAM_SHOP_PLACEHOLDER))
            .displayName(Text.translatable("item_group.socwars.map_making_tools"))
            .build();

    static void initialise() {
        Registry.register(Registries.ITEM_GROUP, ITEMS_KEY, ITEMS_GROUP);
        Registry.register(Registries.ITEM_GROUP, BLOCKS_KEY, BLOCKS_GROUP);
        Registry.register(Registries.ITEM_GROUP, MAP_MAKING_TOOLS_KEY, MAP_MAKING_TOOLS_GROUP);
    }

    static void addItemToItemsGroup(Item item) {
        ItemGroupEvents.modifyEntriesEvent(ITEMS_KEY).register(itemGroup -> itemGroup.add(item));
    }

    static void addItemToBlocksGroup(Item item) {
        ItemGroupEvents.modifyEntriesEvent(BLOCKS_KEY).register(itemGroup -> itemGroup.add(item));
    }

    static void addItemToMapMakingGroup(Item item) {
        ItemGroupEvents.modifyEntriesEvent(MAP_MAKING_TOOLS_KEY).register(itemGroup -> itemGroup.add(item));
    }

    @SafeVarargs
    static void addItemToGroupsAndBaseItemGroup(Item item, RegistryKey<ItemGroup>... itemGroups) {
        ItemGroupEvents.modifyEntriesEvent(ITEMS_KEY).register(_itemGroup -> _itemGroup.add(item));
        for (RegistryKey<ItemGroup> itemGroup : itemGroups) {
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(_itemGroup -> _itemGroup.add(item));
        }
    }
}
