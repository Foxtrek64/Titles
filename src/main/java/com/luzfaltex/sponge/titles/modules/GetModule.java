package com.luzfaltex.sponge.titles.modules;

import com.luzfaltex.sponge.titles.TitleEntry;
import com.luzfaltex.sponge.titles.annotations.Command;
import com.luzfaltex.sponge.titles.services.async.AsyncService;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Command(
        name = "Get",
        permission = "luzfaltex.titles.get.base",
        description = "Allows user to retrieve a user's title"
)
public class GetModule extends CommandModule {

    private Optional<ILuckPermsService> luckPermsService;
    private Optional<IDatabaseService> databaseService;

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
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

        Player player = args.<Player>getOne("target").get();
        boolean sameUser = player.getUniqueId().equals(((Player)src).getUniqueId());

        try {
            CompletableFuture<Integer> titleFuture = luckPermsService.get().getSelectedTitleId(player.getUniqueId());

            int titleId = AsyncService.execute(titleFuture);

            if (-1 == titleId) {
                src.sendMessage(Text.of(Message.GetTitleFailure.getFormattedMessage("Selected title is not an integer.")));
                return CommandResult.empty();
            } else if (0 == titleId) {
                if (sameUser)
                    src.sendMessage(Text.of(Message.SelectedTitleSelf.getFormattedMessage("None")));
                else
                    src.sendMessage(Text.of(Message.SelectedTitleOther.getFormattedMessage(player.getDisplayNameData().displayName(), "None")));
                return CommandResult.success();
            } else {
                Optional<TitleEntry> titleOptional = databaseService.get().getTitle(titleId);

                if (titleOptional.isPresent()) {
                    String titleName = titleOptional.get().Title + "::" + titleOptional.get().Id;
                    if (sameUser)
                        src.sendMessage(Text.of(Message.SelectedTitleSelf.getFormattedMessage(titleName)));
                    else
                        src.sendMessage(Text.of(Message.SelectedTitleOther.getFormattedMessage(player.getDisplayNameData().displayName(), titleName)));
                    return CommandResult.success();
                } else {
                    src.sendMessage(Text.of(Message.GetTitleFailure.getFormattedMessage("Selected title does not exist")));
                    return CommandResult.empty();
                }
            }
        } catch (SQLException se) {
            src.sendMessage(Text.of(Message.GetTitleFailure.getFormattedMessage("Database lookup failed:\n" + se.getMessage())));
            return CommandResult.empty();
        }
    }

    @Override
    public CommandSpec build(CommandSpec.Builder builder) {
        return builder
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("target"))))
                .build();
    }
}
