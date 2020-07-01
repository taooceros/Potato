package Plugins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DeathCoodinate {
    public class commandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                String[] coordinate = Main.db.get_death_coordinate((Player) sender);
                if (coordinate[0] != null) {
                    String msg_c = "&7You last died at &8(&a%x%&7, &a%y%&7, &a%z%&8)&7 in world &a%world%&7."
                            .replaceAll("%x%", coordinate[0]).replaceAll("%y%", coordinate[1])
                            .replaceAll("%z%", coordinate[2]).replaceAll("%world%", coordinate[3]);

                    TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', msg_c));

                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                            String.format("%s %s %s", coordinate[0], coordinate[1], coordinate[2])));
                    msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click to Copy into ClipBoard").color(ChatColor.BLUE).create()));

                    sender.spigot().sendMessage(msg);
                } else {
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
        public void OnDeath(PlayerDeathEvent e) {
            Main.db.update_death_coordinate(e);
        }
    }

}
