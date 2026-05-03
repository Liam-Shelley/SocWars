package com.soc.networking.helper;

import java.util.UUID;
import java.util.stream.Stream;

public interface TeamPlayersProvider {
    Stream<UUID> getPlayersStream();
}
