package com.luzfaltex.sponge.titles.modules;

import com.luzfaltex.sponge.titles.annotations.Command;
import com.luzfaltex.sponge.titles.services.database.IDatabaseService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Command(
        name = "groups",
        permission = "luzfaltex.titles.groups.base",
        description = "Allows user to retrieve a list of current groups"
)
public class GroupsModule extends CommandModule {
    private Optional<IDatabaseService> databaseService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        databaseService = Sponge.getServiceManager().provide(IDatabaseService.class);

        if (databaseService.isPresent()) {
            IDatabaseService dbService = databaseService.get();

            try {
                Set<String> groups = dbService.getTitleGroups();

                src.sendMessage(Text.of(Message.GroupsList.getFormattedMessage()));
                for (String group : groups) {
                    src.sendMessage(Text.of(Message.GroupsListEntry.getFormattedMessage(group)));
                }

                return CommandResult.success();
            } catch (SQLException se) {
                se.printStackTrace();
                return CommandResult.empty();
            }
        } else return CommandResult.empty();
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        return builder.build();
    }
}
