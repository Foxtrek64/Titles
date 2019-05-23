package com.luzfaltex.sponge.titles.modules;

import com.luzfaltex.sponge.titles.annotations.Command;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;

@Command(
        name = "list",
        permission = "luzfaltex.titles.list.base",
        description = "List all available titles"
)

public class ListModule extends CommandModule {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        return null;
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        return builder.build();
    }
}
