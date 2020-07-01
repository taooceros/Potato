package Plugins;

import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    public static Logger logger;
    public static DataBase db;

    @Override
    public void onEnable() {
        logger = getLogger(); // get logger for debug
        db = new DataBase(); // create an instance of a seperate class

        // instanlize and register functions
        DeathCoodinate dc = new DeathCoodinate();
        Bukkit.getPluginManager().registerEvents(dc.new listener(), this);
        Objects.requireNonNull(getCommand("deathcoords")).setExecutor(dc.new commandExecutor());

        UserInfo ui = new UserInfo(this);
        ui.new BackSchaduledTask().runTaskTimerAsynchronously(this, 1200, 144000);
        Objects.requireNonNull(getCommand("get_backup")).setExecutor(ui.new getCommandExecutor());
        Objects.requireNonNull(getCommand("roll_back_user_info")).setExecutor(ui.new rollbackCommandExecutor());
        Bukkit.getPluginManager().registerEvents(ui.new listener(), this);

        Here here = new Here();
        Objects.requireNonNull(getCommand("here")).setExecutor(here.new commandExecutor());

    }

    @Override
    public void onDisable() {
        db.close();
    }
}