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

public class Database {
    private Database() {}

    private static Connection CONNECTION;

    public static void main(String[] args) {
        initialise();
    }

    public static void initialise() {
        try {
            CONNECTION = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "postgres", "postgrespassword");
            Statement statement = CONNECTION.createStatement();

            new BedwarsTable().createSqlTable(statement);
            new SkywarsTable().createSqlTable(statement);
            new LobbyTable().createSqlTable(statement);





        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ServerPlayerEvents.JOIN.register(player -> {
            try {
                Statement statement = CONNECTION.createStatement();
                SocWars.LOGGER.info(new BedwarsTable(player).updateSqlRequest());
                new BedwarsTable(player).blankInsert(statement).updateSql(statement);






            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
