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
import java.util.*;

public class DatabaseService implements IDatabaseService {

    private final SqlService sqlService;
    private final Titles _plugin;
    private static final String Uri = "jdbc:sqlite:titles.sqlite";

    public DatabaseService(Titles plugin) {
        sqlService = Sponge.getServiceManager().provide(SqlService.class).get();
        _plugin = plugin;
    }

    public javax.sql.DataSource getDataSource() throws SQLException {
        return getDataSource(Uri);
    }

    @Override
    public javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        return sqlService.getDataSource(_plugin, jdbcUrl);
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet results = statement.executeQuery()) {

            return results;
        }
    }

    @Override
    public int executeUpdate(String statement) throws SQLException {
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement pStatement = connection.prepareStatement(statement)) {
            return pStatement.executeUpdate();
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
        } catch (SQLException e) {
            _plugin.getLogger().error("SQLException caught when executing query: " + query, e);
            throw e;
        }
    }

    @Override
    public Set<String> getTitleGroups() throws SQLException {
        String query = "SELECT DISTINCT TitleGroup FROM Titles";

        Set<String> groups = new HashSet<>();

        try (ResultSet results = executeQuery(query)) {
            while (results.next()) {
                groups.add(results.getString("TitleGroup"));
            }
        } catch (SQLException se) {
            _plugin.getLogger().error("SQLException caught when executing query: " + query, se);
            throw se;
        }

        return groups;
    }

    @Override
    public boolean createTitle(String titleName, String groupName) throws SQLException {
        groupName = groupName.toLowerCase();

        String query = "INSERT INTO Titles (Title, TitleGroup) VALUES ('{titleName}', '{groupName}')";

        query
                .replace("{titleName}", titleName)
                .replace("{groupName}", groupName);

        try {
            int result = executeUpdate(query);

            if (result == 1) return true;
            else return false;

        } catch (SQLException se) {
            _plugin.getLogger().error("SQLException caught when executing query: " + query, se);
            throw se;
        }
    }
}
