package Plugins;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Here {
    public class commandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0 && sender instanceof Player) {
                Player p = (Player) sender;
                Location loc = p.getLocation();
                Bukkit.spigot()
                        .broadcast(new ComponentBuilder(p.getName() + "'s Location: ").color(ChatColor.BLUE)
                                .append(loc.getWorld().getName().toUpperCase() + " ").color(ChatColor.GOLD)
                                .append(String.format("X: %.0f, Y: %.0f, Z: %.0f", loc.getX(), loc.getY(), loc.getZ()))
                                .color(ChatColor.GREEN).create());

                p.spigot().sendMessage(
                        new ComponentBuilder("You will be highlight for 30 seconds").color(ChatColor.AQUA).create());
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 15));

            } else if (args.length > 0 && sender instanceof Player) {
                List<Player> targets = Bukkit.matchPlayer(String.join(" ", args));
                Player p = (Player) sender;
                Location loc = p.getLocation();
                for (Player target : targets) {

                    target.spigot().sendMessage(new ComponentBuilder(p.getName() + "'s Location: ")
                            .color(ChatColor.BLUE).append(loc.getWorld().getName().toUpperCase() + " ")
                            .color(ChatColor.GOLD)
                            .append(String.format("X: %.0f, Y: %.0f, Z: %.0f", loc.getX(), loc.getY(), loc.getZ()))
                            .color(ChatColor.GREEN).create());

                    p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 15));
                    p.spigot().sendMessage(new ComponentBuilder("You will be highlight for 30 seconds")
                            .color(ChatColor.AQUA).create());

                }
                if (targets.size() == 0)
                    p.sendMessage("Target Player Not Found");

            }
            return true;
        }
    }

}