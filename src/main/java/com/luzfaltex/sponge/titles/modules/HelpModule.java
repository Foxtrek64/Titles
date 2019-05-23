package com.luzfaltex.sponge.titles.modules;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.luzfaltex.sponge.titles.Titles;
import com.luzfaltex.sponge.titles.annotations.Command;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.Set;

@Command(
        name = "help",
        description = "Shows information regarding plugin use",
        permission = "luzfaltex.titles.help.base",
        aliases = "?"
)
public class HelpModule extends CommandModule {

    @Inject
    public Titles plugin;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        // Show help
        Plugin pluginAnnotation = plugin.getClass().getAnnotation(Plugin.class);

        src.sendMessage(Text.of(Message.Info.getFormattedMessage(pluginAnnotation.version())));
        src.sendMessage(Text.of(Message.CommandsList.getFormattedMessage()));

        for (CommandMapping command : Sponge.getCommandManager().getCommands()) {
            String cmdName = command.getPrimaryAlias();
            Set<String> aliases = command.getAllAliases();
            Optional<Text> desc = command.getCallable().getShortDescription(src);
            String cmdDescription = desc.isPresent() ? desc.get().toPlain() : "";

            src.sendMessage(Text.of(Message.CommandsListEntry.getFormattedMessage(cmdName, String.join(", ", aliases), cmdDescription)));
        }

        return CommandResult.success();
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        return builder.build();
    }
}
