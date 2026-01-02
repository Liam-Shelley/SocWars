package com.soc.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.PrimitiveCodec;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public interface Codecs {
    PrimitiveCodec<AtomicInteger> ATOMIC_INT = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<AtomicInteger> read(DynamicOps<T> ops, T input) {
            return ops.getNumberValue(input).map(num -> new AtomicInteger(num.intValue()));
        }

        @Override
        public <T> T write(DynamicOps<T> ops, AtomicInteger value) {
            return ops.createInt(value.intValue());
        }

        @Override
        public String toString() {
            return "AtomicInt";
        }
    };
}
