package com.soc.screenhandler.slots;

public interface SlotConstructor<T extends ShopSlot<?>> {
    T construct();
}
