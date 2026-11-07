package com.soc.util;

public class MutableBoolean {
	private boolean value;

	public MutableBoolean(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public void orEquals(boolean other) {
		this.value |= other;
	}

	public void andEquals(boolean other) {
		this.value &= other;
	}
}
