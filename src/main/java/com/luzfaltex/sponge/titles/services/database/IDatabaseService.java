package com.luzfaltex.sponge.titles.services.database;

import com.luzfaltex.sponge.titles.TitleEntry;
import me.lucko.luckperms.api.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IDatabaseService {
    javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException;
    public ResultSet executeQuery(String query) throws SQLException;
    /**
     * Retrieves a set of titles assigned to the specified title group.
     * @param permissionNode A permission node representing the title group to retrieve
     * @return A collection of {@link TitleEntry}
     */
    List<TitleEntry> getAvailableUserTitles(Node permissionNode) throws SQLException;

    /**
     * Retrieves a TitleEntry using the provided id
     * @param id The unique Id of the title
     * @return A TitleEntry representing the title
     */
    Optional<TitleEntry> getTitle(int id) throws SQLException;
}
