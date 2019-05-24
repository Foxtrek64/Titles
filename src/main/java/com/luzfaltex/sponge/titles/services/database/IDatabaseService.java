package com.luzfaltex.sponge.titles.services.database;

import com.luzfaltex.sponge.titles.TitleEntry;
import me.lucko.luckperms.api.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IDatabaseService {
    /**
     * Retrieves a {@link javax.sql.DataSource} instance using the provided connection url
     * @param jdbcUrl The connection url in driver:type:location.db format
     * @return A configured {@Link javax.sql.DataSource}
     * @throws SQLException If the jdbcUrl is invalid or the database could not be found.
     */
    javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException;

    /**
     * Performs a read-only database query
     * @param query The query statement to look up
     * @return A {@Link ResultSet} containing the results of the lookup
     * @throws SQLException Thrown when the query is invalid or a connection to the database failed.
     */
    ResultSet executeQuery(String query) throws SQLException;

    /**
     * Performs an update statement on the database
     * @param statement The statement to execute
     * @return The row count of modified rows; otherwise, 0.
     * @throws SQLException Thrown when the statement is invalid or a connection to the database failed.
     */
    int executeUpdate(String statement) throws SQLException;

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

    /**
     * Retrieves a Set of Title Groups
     * @return A {@link Set<String>} containing Title Groups
     * @throws SQLException Thrown when the statement is invalid or a connection to the database failed.
     */
    Set<String> getTitleGroups() throws SQLException;

    /**
     * Writes a new title to the database
     * @return True on success; otherwise, false
     * @throws SQLException Thrown when the statement is invalid or a connection to the database failed.
     */
    boolean createTitle(String titleName, String groupName) throws SQLException;
}
