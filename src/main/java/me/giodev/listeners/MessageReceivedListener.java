package me.giodev.listeners;


import me.giodev.FreelanceAssister;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MessageReceivedListener extends ListenerAdapter {

    String[] ids = {"216340083035340801"};
    String[] commands = {"ADD", "GET"};
    String prefix = "-";

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        String command = e.getMessage().getContentRaw().split(" ")[0];
        String[] args = e.getMessage().getContentRaw().split(" ");
        JDA jda = FreelanceAssister.getJDA();
        if (
            e.getAuthor().isBot() ||
            !(isAuthorised(e.getAuthor())) ||
            !(command.startsWith(prefix)) ||
            !(isCommand(command.toUpperCase().replace(prefix, ""))) ||
            e.isFromGuild()
        ) return;

        switch (command.replace(prefix, "").toUpperCase()) {
            case "ADD":
                //TODO
                break;
            case "GET":
                if(args.length < 2) return;

                JSONParser parser = new JSONParser();

                try {
                    Object o = parser.parse(new FileReader(System.getProperty("user.home") + "/documents/self-bot/clients.json"));
                    JSONObject object = (JSONObject) o;

                    try {
                        JSONObject commandObject = (JSONObject) object.get(args[1]);
                        EmbedBuilder eb = new EmbedBuilder();

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        LocalDateTime now = LocalDateTime.now();

                        eb.setTitle(args[1], String.valueOf(commandObject.get("MCMPostLink")));
                        eb.addField(new MessageEmbed.Field("Title", String.valueOf(commandObject.get("Title")), false));
                        eb.addField(new MessageEmbed.Field("Description", String.valueOf(commandObject.get("PluginDescription")), false));
                        eb.addField(new MessageEmbed.Field("Budget", String.valueOf(commandObject.get("Budget")), false));
                        eb.setFooter("@Freelance Assister - " + dtf.format(now), jda.getSelfUser().getAvatarUrl());
                        eb.setColor(Color.CYAN);

                        e.getChannel().sendMessage(eb.build()).queue();

                    }catch (NullPointerException np){
                        System.out.println("aaaa");
                        sendError("Did not find a client with the ID: " + args[1], e.getChannel());
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }

                break;
        }
    }

    public boolean isAuthorised(User author){

        for(String id : ids){
            if(author.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    public boolean isCommand(String command){
        for(String c : commands){
            if(c.equals(command)) return true;
        }
        return false;
    }

    public void sendError(String error, MessageChannel channel){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        JDA jda = FreelanceAssister.getJDA();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("ERROR");
        eb.setDescription(error);
        eb.setFooter("@Freelance Assister - " + dtf.format(now), jda.getSelfUser().getAvatarUrl());

        channel.sendMessage(eb.build()).queue();

    }

}
