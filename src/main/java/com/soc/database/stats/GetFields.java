package com.soc.database.stats;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public interface GetFields {
    default List<Field> getFields() {
        final List<List<Field>> fields2 = new ArrayList<>();
        for (Class<?> clazz = this.getClass(); clazz.getSuperclass() != null; clazz = clazz.getSuperclass()) {
            Collections.addAll(fields2, List.of(clazz.getDeclaredFields()));
        }
        return fields2.reversed().stream().flatMap(List::stream).filter(field -> !field.isAnnotationPresent(IgnoredField.class)).toList();
    }

    default Iterator<String> getValidFields(Function<Field, Optional<String>> fieldMapFunction) {
        return this.getFields().stream().map(fieldMapFunction).filter(Optional::isPresent).map(Optional::get).iterator();
    }
}
