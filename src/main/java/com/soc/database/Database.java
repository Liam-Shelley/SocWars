package com.soc.database;

import com.soc.SocWars;
import com.soc.database.stats.BedwarsTable;
import com.soc.database.stats.LobbyTable;
import com.soc.database.stats.SkywarsTable;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public final class Database {
    private Database() {}

    private static final Connection CONNECTION;
    private static final Statement STATEMENT;

    public static Optional<Statement> getStatement() {
        return Optional.ofNullable(STATEMENT);
    }

    static {
        Connection connection;
        Statement statement;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgrespassword");
            statement = connection.createStatement();
        } catch (SQLException e) {
            connection = null;
            statement = null;

            SocWars.LOGGER.error("Failed to connect to database");
        }
        CONNECTION = connection;
        STATEMENT = statement;
    }

    public static void main(String[] args) {
        initialise();
    }

    public static void initialise() {
        new SkywarsTable().createSqlTable(STATEMENT);
        new BedwarsTable().createSqlTable(STATEMENT);
        new LobbyTable().createSqlTable(STATEMENT);

        ServerPlayerEvents.JOIN.register(player -> {
                new LobbyTable(player).blankInsert(STATEMENT);
                new SkywarsTable(player).blankInsert(STATEMENT);
                new BedwarsTable(player).blankInsert(STATEMENT);
        });
    }
}
