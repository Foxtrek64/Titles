package com.luzfaltex.sponge.titles.modules;

import com.luzfaltex.sponge.titles.TitleEntry;
import com.luzfaltex.sponge.titles.annotations.Command;
import com.luzfaltex.sponge.titles.services.database.IDatabaseService;
import com.luzfaltex.sponge.titles.services.luckperms.ILuckPermsService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.Optional;

@Command(
        name = "Set",
        aliases = "use",
        permission = "luzfaltex.titles.set.base",
        description = "Allows user to set their own title"
)
public class SetModule extends CommandModule {

    private Optional<ILuckPermsService> luckPermsService;
    private Optional<IDatabaseService> databaseService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        luckPermsService = Sponge.getServiceManager().provide(ILuckPermsService.class);
        databaseService = Sponge.getServiceManager().provide(IDatabaseService.class);

        if (!luckPermsService.isPresent()) {
            src.sendMessage(Text.of(Message.SetTitleFailure.getFormattedMessage("Could not interface with LuckPerms.")));
            return CommandResult.empty();
        }
        if (!databaseService.isPresent()) {
            src.sendMessage(Text.of(Message.SetTitleFailure.getFormattedMessage("Could not interface with the database service.")));
            return CommandResult.empty();
        }

        int id = args.<Integer>getOne("id").get();
        Player player = args.<Player>getOne("target").get();

        try {
            Optional<TitleEntry> title = databaseService.get().getTitle(id);

            if (title.isPresent()) {
                luckPermsService.get().setSelectedTitle(player.getUniqueId(), title.get());

                src.sendMessage(Text.of(Message.SetTitleSuccess.getFormattedMessage(title.get().Title)));
            } else {
                src.sendMessage(Text.of(Message.SetTitleFailure.getFormattedMessage("A title with id " + id + " could not be found.")));
            }
            return CommandResult.success();

        } catch (SQLException e) {
            src.sendMessage(Text.of(Message.SetTitleFailure.getFormattedMessage("SQL Lookup failed:\n" + e.getMessage())));
            return CommandResult.empty();
        }
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        return builder.arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("target"))),
                GenericArguments.onlyOne(GenericArguments.integer(Text.of("id"))))
                .build();
    }
}
