package com.soc.gui.hud;

public class Reference<T> {
    public Reference(T value) {
        this.value = value;
    }

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public boolean isNotNull() {
        return this.value != null;
    }
}
