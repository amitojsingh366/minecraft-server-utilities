package net.amitoj.minecraftUtilities.util;

import net.amitoj.minecraftUtilities.MinecraftUtilities;
import org.bukkit.entity.Player;

import java.sql.*;

public class Database {
    private MinecraftUtilities _plugin;
    private Connection _connection;
    public String databaseUrl;


    public Database(MinecraftUtilities plugin) {
        this._plugin = plugin;
        this.databaseUrl = "jdbc:sqlite:" + _plugin.getDataFolder().getAbsolutePath() + "/players.db";

        connect();
        createNewTable();
    }


    private void connect() {
        _connection = null;
        try {
            _connection = DriverManager.getConnection(databaseUrl);
            _connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS players (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	uuid text NOT NULL,\n"
                + "	discordId text NOT NULL,\n"
                + "	ip text NOT NULL\n"
                + ");";

        Statement stmt = null;
        try {
            stmt = _connection.createStatement();
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void checkPlayer(Player player) {
        String sql = "SELECT EXISTS(SELECT * FROM players WHERE uuid = ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = _connection.prepareStatement(sql);
            pstmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (!rs.getBoolean(1)) {
                    String insert_sql = "INSERT INTO players(uuid, discordId, ip) VALUES(?, ?, ?)";
                    PreparedStatement insert_pstmt = null;

                    insert_pstmt = _connection.prepareStatement(insert_sql);
                    insert_pstmt.setString(1, player.getUniqueId().toString());
                    insert_pstmt.setString(2, "");
                    insert_pstmt.setString(3, "");

                    insert_pstmt.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public PlayerData getPlayerData(Player player) {
        checkPlayer(player);

        String sql = "SELECT * FROM players WHERE uuid = ?";
        PreparedStatement pstmt = null;

        try {
            pstmt = _connection.prepareStatement(sql);
            pstmt.setString(1, player.getUniqueId().toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PlayerData(rs.getString("uuid"),
                        rs.getString("discordId"),
                        rs.getString("ip"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return new PlayerData(player.getUniqueId().toString(), "", "");
    }

    public void updatePlayerData(PlayerData playerData) {
        String sql = "UPDATE players SET discordId = ?, ip = ? WHERE uuid = ?";
        PreparedStatement pstmt = null;
        try {
            pstmt = _connection.prepareStatement(sql);
            pstmt.setString(1, playerData.discordId);
            pstmt.setString(2, playerData.ip);
            pstmt.setString(3, playerData.uuid);

            pstmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void disconnect() {
        if (_connection != null) {
            try {
                _connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}

