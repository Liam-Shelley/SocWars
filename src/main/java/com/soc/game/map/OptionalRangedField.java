package com.soc.game.map;

public record OptionalRangedField<T extends Comparable<T>>(String name, T minValue, T maxValue) {

}
