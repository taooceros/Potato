package Plugins;

import java.io.File;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DataBase {
    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (Exception ex) {
            App.logger.info(ex.getMessage());
        }
        File dir = new File(Config.path);
        if (!dir.exists())
            dir.mkdir();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Config.path + "/database.db");
                Statement stat = conn.createStatement()) {
            try (ResultSet dc_t = conn.getMetaData().getTables(null, null, "death_coordinate", null);
                    ResultSet ui_t = conn.getMetaData().getTables(null, null, "user_info", null)) {
                if (!dc_t.next())
                    stat.executeUpdate(
                            "CREATE TABLE death_coordinate (uuid CHAR (16) PRIMARY KEY NOT NULL,x INTEGER, y INTEGER, z INTEGER, world VARCHAR(12));");
                if (!ui_t.next()) {
                    stat.executeUpdate(
                            "CREATE TABLE user_info (uuid NOT NULL,info BLOB NOT NULL,date DATETIME NOT NULL)");
                }
            } catch (Exception ex) {
                App.logger.info(ex.getMessage());
            }

        } catch (Exception ex) {
            App.logger.info(ex.getMessage());
        }
    }

    public String[] get_death_coordinate(Player p) {
        String[] result = new String[4];
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Config.path + "/database.db");
                Statement stat = conn.createStatement()) {
            ResultSet rs = stat.executeQuery(String.format("select x,y,z,world from death_coordinate where uuid='%s'",
                    p.getUniqueId().toString()));
            App.logger.info(String.format("select x,y,z,world from death_coordinate where uuid='%s'",
                    p.getUniqueId().toString()));
            if (rs.next()) {
                for (int i = 0; i < 4; i++) {
                    result[i] = rs.getString(i + 1);
                }
            }
        } catch (Exception ex) {
            App.logger.info(ex.getMessage());
        }
        return result;
    }

    public void update_death_coordinate(PlayerDeathEvent deathEvent) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Config.path + "/database.db");
                Statement stat = conn.createStatement()) {

            try (ResultSet rs = stat.executeQuery(String.format("select * from death_coordinate where uuid='%s'",
                    deathEvent.getEntity().getUniqueId()))) {
                if (rs.next()) {
                    String uuid = deathEvent.getEntity().getUniqueId().toString();
                    Location loc = deathEvent.getEntity().getLocation();
                    stat.executeUpdate(
                            String.format("update death_coordinate set x=%d,y=%d,z=%d, world='%s' where uuid = '%s'",
                                    (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), loc.getWorld().getName(),
                                    uuid.toString()));
                } else {
                    String uuid = deathEvent.getEntity().getUniqueId().toString();
                    Location loc = deathEvent.getEntity().getLocation();
                    stat.executeUpdate(String.format(
                            "insert into death_coordinate (uuid,x,y,z,world) values ('%s',%d,%d,%d,%s)", uuid,
                            (int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), loc.getWorld().getName()));
                }

            }

        } catch (Exception ex) {
            App.logger.info(ex.getMessage());
        }
    }

    public void insert_user_info(byte[] info, String uuid) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + Config.path + "/database.db");
                PreparedStatement prep = conn
                        .prepareStatement("insert into user_info (uuid,info,date_time) values (?,?,?)");) {
            prep.setString(1, uuid);
            prep.setBytes(2, info);

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String Now = df.format(new java.util.Date());
            prep.setString(3, Now);
            prep.executeUpdate();
            
            
        } catch (Exception ex) {

        }
    }
}