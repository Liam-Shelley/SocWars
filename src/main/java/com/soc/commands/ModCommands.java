package com.soc.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public interface ModCommands {
    static void initialise() {
        CommandRegistrationCallback.EVENT.register(EndGame::register);
        CommandRegistrationCallback.EVENT.register(Collectibles::register);

        GameIdArgumentType.initialise();
    }
}
