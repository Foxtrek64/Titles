package com.luzfaltex.sponge.titles;

import com.google.inject.Inject;
import com.luzfaltex.sponge.titles.services.async.AsyncService;
import com.luzfaltex.sponge.titles.services.commands.CommandService;
import com.luzfaltex.sponge.titles.services.commands.ICommandService;
import com.luzfaltex.sponge.titles.services.database.DatabaseService;
import com.luzfaltex.sponge.titles.services.database.IDatabaseService;
import com.luzfaltex.sponge.titles.services.luckperms.ILuckPermsService;
import com.luzfaltex.sponge.titles.services.luckperms.LuckPermsService;
import me.rojo8399.placeholderapi.ExpansionBuilder;
import me.rojo8399.placeholderapi.Placeholder;
import me.rojo8399.placeholderapi.PlaceholderService;
import me.rojo8399.placeholderapi.Source;
import me.rojo8399.placeholderapi.impl.PlaceholderServiceImpl;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.sql.SQLException;
import java.util.Optional;


@Plugin(
        id = "titles",
        name = "Titles",
        description = "A simple plugin which allows for the display of a title or occupation.",
        url = "https://github.com/ravenrockrp/Titles",
        version = "0.1-API7",
        authors = {
                "Foxtrek_64"
        },
        dependencies = {
                @Dependency(id = "placeholderapi"),
                @Dependency(id = "luckperms")
        }
)
public class Titles {

    @Inject
    private Logger _logger;

    private PlaceholderService _placeholderService;
    private ILuckPermsService _luckPermsService;
    private ICommandService _commandService;
    private IDatabaseService _databaseService;

    // init services here
    @Listener
    public void onServerInitialization(GameInitializationEvent event) {
        _commandService = new CommandService(this);
        _luckPermsService = new LuckPermsService(this);
        _databaseService = new DatabaseService(this);

        Sponge.getServiceManager().setProvider(this, ICommandService.class, _commandService);
        Sponge.getServiceManager().setProvider(this, ILuckPermsService.class, _luckPermsService);
        Sponge.getServiceManager().setProvider(this, IDatabaseService.class, _databaseService);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        _commandService.registerCommands();

        _placeholderService = Sponge.getServiceManager().provide(PlaceholderService.class).get();

        _placeholderService.loadAll(this, this).stream()
                .map(builder ->
                    builder.description("Returns the player's selected Id"))
                .map(builder -> builder.author("Foxtrek_64").version("1.0"))
                .forEach(builder -> {
                    try {
                        builder.buildAndRegister();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    public Logger getLogger() {
        return _logger;
    }

    @Placeholder(id = "title")
    public String getTitle(@Source Player player) {
        if (Optional.of(_databaseService).isPresent() && Optional.of(_luckPermsService).isPresent()) {
            int titleId = AsyncService.execute(_luckPermsService.getSelectedTitleId(player.getUniqueId()));
            if (-1 == titleId || 0 == titleId)
                return "";
            try {
                Optional<TitleEntry> title = _databaseService.getTitle(titleId);
                if (title.isPresent())
                    return title.get().Title;
                return "";
            } catch (SQLException se) {
                _logger.error("SQLException while getting title", se);
                return "";
            }
        } else return "";
    }
}
