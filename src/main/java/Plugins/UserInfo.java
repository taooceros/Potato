package Plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitRunnable;

public class UserInfo {
    String player_data_path = "world/playerdata/";

    public class commandExecutor implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0 && sender instanceof Player) {
                Player p = (Player) sender;
                File user_info = new File(player_data_path + p.getUniqueId() + ".dat");
                byte[] data;
                try (FileInputStream inputStream = new FileInputStream(user_info)) {
                    data = new byte[inputStream.available()];
                    inputStream.read(data);
                    App.db.insert_user_info(data, p.getUniqueId().toString());
                    try (FileOutputStream output = new FileOutputStream(p.getUniqueId().toString() + ".dat")) {
                        output.write(data);
                    } catch (Exception ex) {

                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }

                return true;
            } else {
                sender.sendMessage("Be a player to get your own data");
                return true;
            }
        }
    }

    public class SchaduledTask extends BukkitRunnable {
        JavaPlugin plugin;
        Player p;

        public SchaduledTask(JavaPlugin plugin, Player p) {
            this.plugin = plugin;
            this.p = p;
        }

        @Override
        public void run() {
            if (Bukkit.getServer().getOnlinePlayers().contains(p)) {
                File user_info = new File(player_data_path + p.getUniqueId() + ".dat");
                byte[] data;
                try (FileInputStream inputStream = new FileInputStream(user_info)) {
                    data = new byte[inputStream.available()];
                    inputStream.read(data);
                    App.db.insert_user_info(data, p.getUniqueId().toString());

                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
            else{
                this.cancel();
            }
            
        }
    }
}