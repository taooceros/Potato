package Plugins;

import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class App extends JavaPlugin implements Listener {
    public static Logger logger;
    public static DataBase db;

    @Override
    public void onEnable() {
        logger = getLogger();
        db = new DataBase();
        db.initialize();

        DeathCoodinate dc = new DeathCoodinate();
        Bukkit.getPluginManager().registerEvents(dc.new listener(), this);
        Objects.requireNonNull(getCommand("deathcoords")).setExecutor(dc.new commandExecutor());

        UserInfo ui = new UserInfo(this);
        ui.new BackSchaduledTask().runTaskTimer(this, 1200, 14400);
        Objects.requireNonNull(getCommand("get_backup")).setExecutor(ui.new getCommandExecutor());
        Objects.requireNonNull(getCommand("roll_back_user_info")).setExecutor(ui.new rollbackCommandExecutor());;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");

    }
}