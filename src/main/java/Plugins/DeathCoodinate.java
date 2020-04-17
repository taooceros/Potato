package Plugins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.md_5.bungee.api.ChatColor;

public class DeathCoodinate {
    public class commandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                String[] coordinate = App.db.get_death_coordinate((Player) sender);
                if (coordinate[0] != null) {
                    String msg = "&7You last died at &8(&a%x%&7, &a%y%&7, &a%z%&8)&7 in world &a%world%&7."
                            .replaceAll("%x%", coordinate[0]).replaceAll("%y%", coordinate[1])
                            .replaceAll("%z%", coordinate[2]).replaceAll("%world%", coordinate[3]);

                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                else{
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7No Record&8"));
                }
                return true;
            } else {
                sender.sendMessage("you must be a player");
                return true;
            }
        }
    }

    public class listener implements Listener {
        @EventHandler
        public void OnDeath(final PlayerDeathEvent e) {
            App.db.update_death_coordinate(e);
        }
    }

}
