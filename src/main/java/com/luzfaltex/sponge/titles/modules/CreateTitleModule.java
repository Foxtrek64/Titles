package com.luzfaltex.sponge.titles.modules;

import com.luzfaltex.sponge.titles.annotations.Command;
import com.luzfaltex.sponge.titles.services.database.IDatabaseService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

@Command(
        name = "title",
        permission = "luzfaltex.titles.create.title.base",
        description = "Allows user to create a new title"
)
public class CreateTitleModule extends CommandModule {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<IDatabaseService> databaseService = Sponge.getServiceManager().provide(IDatabaseService.class);

        if (databaseService.isPresent()) {
            IDatabaseService dbService = databaseService.get();
            try {
                Optional<String> titleNameOpt = args.getOne("titleName");
                Optional<String> titleGroupOpt = args.getOne("titleGroup");

                if (!titleNameOpt.isPresent() || !titleGroupOpt.isPresent())
                    return CommandResult.empty();

                String titleName = titleNameOpt.get();
                String titleGroup = titleGroupOpt.get();

                dbService.createTitle(titleName, titleGroup);
                return CommandResult.success();
            } catch (SQLException se) {
                return CommandResult.empty();
            }
        }
        else {
            return CommandResult.empty();
        }
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        Optional<IDatabaseService> databaseService = Sponge.getServiceManager().provide(IDatabaseService.class);

        if (databaseService.isPresent()) {
            IDatabaseService dbService = databaseService.get();
            try {

                Set<String> groups = dbService.getTitleGroups();

                return builder
                        .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("titleName"))))
                        .arguments(GenericArguments.onlyOne(GenericArguments.withSuggestions(GenericArguments.string(Text.of("titleGroup")), groups, true)))
                        .build();
            } catch (SQLException se) {
                se.printStackTrace();
                return builder.build();
            }
        } else return builder.build();
    }
}
