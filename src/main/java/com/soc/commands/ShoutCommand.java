package com.soc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.GamesManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

import static com.soc.lib.SocWarsLib.colouredTextFromColour;

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
            return -1;
        }

        final AbstractGameManager<?, ?, ?> manager = managerOptional.get();

        try {
            final Text rawMessage = MessageArgumentType.getMessage(context, MESSAGE_ARGUMENT);

            if (context.getSource().getPlayer() == null) {
                manager.broadcast(Text.translatable("message.shout.chat_message.non_player_source", rawMessage), false);
            } else {
                manager.broadcast(Text.translatable("message.shout.chat_message", context.getSource().getPlayer().getDisplayName(), rawMessage), false);
            }
            return 0;
        } catch (CommandSyntaxException e) {
            context.getSource().sendMessage(Text.translatable("message.shout.failed"));
            return -1;
        }
    }
}
