package com.luzfaltex.sponge.titles.services.commands;

import com.google.common.collect.ObjectArrays;
import com.luzfaltex.sponge.titles.modules.CommandModule;
import com.luzfaltex.sponge.titles.Titles;
import com.luzfaltex.sponge.titles.annotations.Command;
import com.luzfaltex.sponge.titles.modules.InfoModule;
import org.reflections.Reflections;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Set;

public class CommandService implements ICommandService {

    private final Titles _plugin;

    public CommandService(Titles plugin) {
        _plugin = plugin;
    }

    @Override
    public void registerCommands() {
        // Dynamically load all CommandExecutors
        Reflections reflections = new Reflections("com.luzfaltex.sponge.titles");

        Set<Class<? extends CommandModule>> subTypes = reflections.getSubTypesOf(CommandModule.class);

        CommandSpec.Builder masterSpec = CommandSpec.builder()
                .permission("luzfaltex.titles.base")
                .description(Text.of("Base command for Titles"))
                .executor(new InfoModule()::execute);

        for (Class<? extends CommandModule> command : subTypes) {
            Command commandAnnotation = command.getAnnotation(Command.class);

            if (commandAnnotation.reflectionIgnore()) continue;

            CommandModule cmd = command.cast(CommandModule.class);

            CommandSpec.Builder builder = CommandSpec.builder()
                    .description(Text.of(commandAnnotation.description()))
                    .permission(commandAnnotation.permission())
                    .executor(cmd::execute);
            String[] commandWithAliases = ObjectArrays.concat(commandAnnotation.name(), commandAnnotation.aliases());

            masterSpec.child(cmd.build(builder), commandWithAliases);
        }

        Sponge.getCommandManager().register(_plugin, masterSpec.build(), "titles");
    }
}
