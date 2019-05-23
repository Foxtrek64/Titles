package com.luzfaltex.sponge.titles.modules;

import com.google.inject.Inject;
import com.luzfaltex.sponge.titles.Titles;
import com.luzfaltex.sponge.titles.annotations.Command;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

@Command(
        name = "info",
        description = "Shows plugin information",
        permission = "luzfaltex.titles.base"
)
public class InfoModule extends CommandModule {

    @Inject
    public Titles plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Show help
        Plugin pluginAnnotation = plugin.getClass().getAnnotation(Plugin.class);

        src.sendMessage(Text.of(Message.Info.getFormattedMessage(pluginAnnotation.version())));
        src.sendMessage(Text.of(Message.ViewAvailableCommands.getFormattedMessage("titles")));

        return CommandResult.success();
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        return builder.build();
    }
}
