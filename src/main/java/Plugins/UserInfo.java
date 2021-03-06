package Plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class UserInfo {
    String player_data_path = "world/playerdata/";
    JavaPlugin plugin;

    public UserInfo(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public class getCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ArrayList<String> dateList = Main.db.get_user_info_list(p.getUniqueId().toString());
                if (dateList != null) {

                    for (String date : dateList) {
                        TextComponent text = new TextComponent(date);
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("Click to auto suggest rollback command").color(ChatColor.BLUE)
                                        .create()));
                        text.setClickEvent(
                                new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/roll_back_user_info " + date));
                        sender.spigot().sendMessage(text);
                    }
                } else {
                    sender.sendMessage("No information");
                }

                sender.spigot()
                        .sendMessage(new ComponentBuilder("You still left ").color(ChatColor.AQUA)
                                .append(Integer.toString(Main.db.get_remaining_oppotunity(p.getUniqueId().toString())))
                                .color(ChatColor.RED).append(" Oppotunity to rollback in this month")
                                .color(ChatColor.AQUA).create());

                return true;

            }
            // when the console want to get the user data
            else if (args.length > 0) {
                ArrayList<String> dateList = Main.db.get_user_info_list(args[0]);
                if (dateList != null)
                    for (String date : dateList) {
                        sender.sendMessage(date);
                    }
                else {
                    sender.sendMessage("No information");
                }

                sender.spigot()
                        .sendMessage(new ComponentBuilder("You still left ").color(ChatColor.AQUA)
                                .append(Integer.toString(Main.db.get_remaining_oppotunity(args[0]))).color(ChatColor.RED)
                                .append(" Oppotunity to rollback in this month").color(ChatColor.AQUA).create());
                return true;
            } else
                return false;
        }
    }

    public class rollbackCommandExecutor implements CommandExecutor {

        public LinkedList<RollbackInfo> waitingConfirm = new LinkedList<RollbackInfo>();

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player && (args.length != 1 || !args[0].equals("confirm"))) {
                Player p = (Player) sender;
                Object[] data = Main.db.get_user_info(p.getUniqueId().toString(), String.join(" ", args));

                if (data != null & Main.db.get_remaining_oppotunity(p.getUniqueId().toString()) > 0) {

                    sender.sendMessage("You are going to rollback your information to the one stored at " + data[0]);

                    sender.spigot().sendMessage(
                            new ComponentBuilder("Enter /roll_back_user_info confirm to confirm the rollback task")
                                    .color(ChatColor.RED)
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/roll_back_user_info "))
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                            "Click to auto suggest rollback command (you should mannually enter the confirm)")
                                                    .color(ChatColor.AQUA).create()))
                                    .create());

                    sender.spigot().sendMessage(new ComponentBuilder("You still left ").color(ChatColor.AQUA)
                            .append(Integer.toString(Main.db.get_remaining_oppotunity(p.getUniqueId().toString())))
                            .color(ChatColor.RED).append(" Oppotunity to rollback in this month").color(ChatColor.AQUA)
                            .create());

                    waitingConfirm.offer(new RollbackInfo(p.getUniqueId().toString(), (byte[]) data[1]));

                    this.new waitingRunnerable().runTaskLater(plugin, 200);
                } else if (data == null) {
                    sender.sendMessage("Wrong input of datetime for rollback info");
                } else {
                    sender.spigot()
                            .sendMessage(new ComponentBuilder("You don't have enough oppotunity to rollback your info")
                                    .color(ChatColor.RED).create());
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                for (RollbackInfo info : waitingConfirm) {
                    if (info.uuid.equals(p.getUniqueId().toString())) {
                        p.kickPlayer("Rollback Need");

                        // Create a runnerable instance to do task after 20 tick
                        // so that the server can save the user_info before rollback
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try (FileOutputStream output = new FileOutputStream(
                                        player_data_path + p.getUniqueId() + ".dat")) {
                                    output.write(info.data);
                                } catch (Exception ex) {
                                    Main.logger.info(ex.getMessage());
                                }
                                Main.db.reduce_count(p.getUniqueId().toString());
                            }
                        }.runTaskLater(plugin, 20);

                    }
                }
            }
            return true;
        }

        public class RollbackInfo {
            public String uuid;
            public byte[] data;

            public RollbackInfo(String uuid, byte[] data) {
                this.uuid = uuid;
                this.data = data;
            }
        }

        public class waitingRunnerable extends BukkitRunnable {
            @Override
            public void run() {
                waitingConfirm.removeFirst();
            }
        }
    }

    public class listener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    File user_info = new File(player_data_path + playerJoinEvent.getPlayer().getUniqueId() + ".dat");
                    byte[] data;
                    try (FileInputStream inputStream = new FileInputStream(user_info)) {
                        data = new byte[inputStream.available()];
                        inputStream.read(data);
                        Main.db.insert_user_info(data, playerJoinEvent.getPlayer().getUniqueId().toString());
                        // App.logger.info("Backup " + p.getUniqueId());
                        playerJoinEvent.getPlayer().sendMessage("Your Information has been backup now.");
                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
            }.runTaskLaterAsynchronously(plugin, 100);
        }
    }

    public class BackSchaduledTask extends BukkitRunnable {
        JavaPlugin plugin;
        Player p;

        @Override
        public void run() {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                File user_info = new File(player_data_path + p.getUniqueId() + ".dat");
                byte[] data;
                try (FileInputStream inputStream = new FileInputStream(user_info)) {
                    data = new byte[inputStream.available()];
                    inputStream.read(data);
                    Main.db.insert_user_info(data, p.getUniqueId().toString());
                    // App.logger.info("Backup " + p.getUniqueId());
                    p.sendMessage("Your Information has been backup now.");
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        }
    }
}