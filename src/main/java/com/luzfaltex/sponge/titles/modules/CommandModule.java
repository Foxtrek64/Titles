package com.luzfaltex.sponge.titles.modules;

import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public abstract class CommandModule implements CommandExecutor {
    public abstract CommandSpec build(CommandSpec.Builder builder);
}
