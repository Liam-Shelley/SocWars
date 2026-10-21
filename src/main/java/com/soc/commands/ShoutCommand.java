package com.soc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.GamesManager;
import com.soc.player.CollectiblesManager;
import com.soc.player.PlayerDataManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ShoutCommand {
    String SHOUT_COMMAND_ID = "shout";
    String MESSAGE_ARGUMENT = "message";

    static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
            dispatcher.register(CommandManager.literal(SHOUT_COMMAND_ID)
                .then(
                    CommandManager.argument(MESSAGE_ARGUMENT, MessageArgumentType.message())
                        .executes(ShoutCommand::execute)
                    )
            );
    }

    static int execute(CommandContext<ServerCommandSource> context) {
        final Optional<AbstractGameManager<?, ?, ?>> managerOptional = GamesManager.getInstance().getGame(context.getSource().getEntity());
        if (managerOptional.isEmpty()) {
            context.getSource().sendMessage(Text.translatable("message.shout.not_in_game"));
            return 0;
        }

        return 0;
    }
}
