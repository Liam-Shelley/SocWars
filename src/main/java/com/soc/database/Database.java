package com.soc.database;

import com.soc.SocWars;
import com.soc.database.stats.BedwarsTable;
import com.soc.database.stats.LobbyTable;
import com.soc.database.stats.SkywarsTable;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

import java.sql.Connection;
import java.sql.DriverManager;
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
        } catch (Exception e) {
            connection = null;
            statement = null;

            SocWars.LOGGER.error("Failed to connect to database\n{}", e.getMessage());
        }
        CONNECTION = connection;
        STATEMENT = statement;

        SocWars.LOGGER.info("Database successfully connected!");
    }

    public static void initialise() {
        new LobbyTable().createSqlTable(STATEMENT);
        new SkywarsTable().createSqlTable(STATEMENT);
        new BedwarsTable().createSqlTable(STATEMENT);

        ServerPlayerEvents.JOIN.register(player -> {
                new LobbyTable(player).blankInsert(STATEMENT);
                new SkywarsTable(player).blankInsert(STATEMENT);
                new BedwarsTable(player).blankInsert(STATEMENT);
        });
    }
}
