package com.luzfaltex.sponge.titles.services.database;

import com.luzfaltex.sponge.titles.TitleEntry;
import com.luzfaltex.sponge.titles.Titles;
import me.lucko.luckperms.api.Node;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseService implements IDatabaseService {

    private SqlService sqlService;
    private final Titles _plugin;

    public DatabaseService(Titles plugin) {
        Sponge.getServiceManager().provide(SqlService.class).get();
        _plugin = plugin;
    }

    @Override
    public javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        return sqlService.getDataSource(jdbcUrl);
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        String uri = "jdbc:sqlite:titles.sqlite";

        try (Connection connection = getDataSource(uri).getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            return results;
        }
    }

    @Override
    public List<TitleEntry> getAvailableUserTitles(Node permissionNode) throws SQLException {
        String[] pieces = permissionNode.getPermission().split(".");
        String titleGroup = pieces[pieces.length - 1];
        String query = "SELECT * FROM Titles WHERE TitleGroup = \"" + titleGroup + "\"";
        try (ResultSet results = executeQuery(query)) {

            List<TitleEntry> entries = new ArrayList<>();

            while (results.next()) {
                TitleEntry entry = TitleEntry.builder()
                        .withId(results.getInt("Id"))
                        .withTitle(results.getString("Title"))
                        .withGroup(results.getString("TitleGroup"))
                        .build();
                entries.add(entry);
            }

            return entries;

        } catch (SQLException e) {
            _plugin.getLogger().error("SQLException caught when executing query: " + query, e);
            throw e;
        }
    }

    @Override
    public Optional<TitleEntry> getTitle(int id) throws SQLException {
        String query = "SELECT * FROM Titles WHERE Id = " + id;

        try (ResultSet results = executeQuery(query)) {
            if (results.first()) {
                return Optional.of(TitleEntry.builder()
                        .withId(results.getInt("Id"))
                        .withTitle(results.getString("Title"))
                        .withGroup(results.getString("TitleGroup"))
                        .build());
            } else return Optional.empty();
        }
    }
}
