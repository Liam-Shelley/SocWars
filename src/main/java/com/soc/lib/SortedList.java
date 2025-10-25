package com.soc.lib;

import java.util.ArrayList;
import java.util.Collections;

public class SortedList<T extends Comparable<T>> extends ArrayList<T> {
    @Override
    public boolean add(T o) {
        final int index = Collections.binarySearch(this, o);
        super.add(index < 0 ? -index - 1 : index, o);

        return true;
    }
}
