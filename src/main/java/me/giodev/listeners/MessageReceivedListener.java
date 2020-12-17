package me.giodev.listeners;


import me.giodev.FreelanceAssister;
import me.giodev.misc.ClientObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MessageReceivedListener extends ListenerAdapter {

    private ClientObject client;
    private int addStage;
    String[] ids = {"216340083035340801"};
    String[] commands = {"ADD", "GET"};
    String prefix = "-";

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        String command = e.getMessage().getContentRaw().split(" ")[0];
        String[] args = e.getMessage().getContentRaw().split(" ");
        JDA jda = FreelanceAssister.getJDA();

        if(addStage != 0 && !e.getAuthor().isBot()){
            advanceStage(args, addStage, e.getChannel());
            return;
        }

        if (
            e.getAuthor().isBot() ||
            !(isAuthorised(e.getAuthor())) ||
            !(command.startsWith(prefix)) ||
            !(isCommand(command.toUpperCase().replace(prefix, ""))) ||
            e.isFromGuild()
        ) return;

        try {
            JSONParser parser = new JSONParser();
            Object o = parser.parse(new FileReader(System.getProperty("user.home") + "/documents/self-bot/clients.json"));
            JSONObject object = (JSONObject) o;

        switch (command.replace(prefix, "").toUpperCase()) {
            case "ADD":
                if (args.length < 3) return;

                client = new ClientObject(args[1], args[2]);
                advanceStage(args, addStage, e.getChannel());

                break;
            case "GET":

                if (args.length < 2) return;

                try {
                    JSONObject commandObject = (JSONObject) object.get(args[1]);

                    EmbedBuilder getEmbed = new EmbedBuilder();

                    String description = String.valueOf(commandObject.get("PluginDescription"));

                    try {

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        LocalDateTime now = LocalDateTime.now();

                        getEmbed.setTitle(args[1], String.valueOf(commandObject.get("MCMPostLink")));
                        getEmbed.addField(new MessageEmbed.Field("Title", String.valueOf(commandObject.get("Title")), false));
                        getEmbed.addField(new MessageEmbed.Field("Description", description, false));
                        getEmbed.addField(new MessageEmbed.Field("Budget", String.valueOf(commandObject.get("Budget")), false));
                        getEmbed.setFooter("@Freelance Assister - " + dtf.format(now), jda.getSelfUser().getAvatarUrl());
                        getEmbed.setColor(Color.CYAN);

                        e.getChannel().sendMessage(getEmbed.build()).queue();

                    } catch (IllegalArgumentException ie) {
                        sendError("The description cannot be longer than 1024 characters (" + description.length() + " chars)", e.getChannel());
                    }

                } catch (NullPointerException np) {
                    System.out.println("aaaa");
                    sendError("Did not find a client with the ID: " + args[1], e.getChannel());
                }
                break;
            }
        }catch (ParseException | IOException ex){
            ex.printStackTrace();
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

    private void advanceStage(String args[], int stage, MessageChannel mc){

        String fullMessage = "";

        for(String s : args){
            fullMessage += s + " ";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        JDA jda = FreelanceAssister.getJDA();
        EmbedBuilder addEmbed = new EmbedBuilder();

        addEmbed.setColor(Color.CYAN);
        addEmbed.setFooter("@Freelance Assister - " + dtf.format(now), jda.getSelfUser().getAvatarUrl());

        addEmbed.setTitle(client.getDiscordID(), client.getUrl());

        try {
            switch (stage) {
                case 0:
                    addEmbed.addField(new MessageEmbed.Field("Title", "Please type in the title", false));
                    addStage++;
                    break;
                case 1:
                    addEmbed.clearFields();
                    addEmbed.addField(new MessageEmbed.Field("Title", fullMessage, false));
                    addEmbed.addField(new MessageEmbed.Field("Description", "Please type in the description", false));

                    client.setTitle(fullMessage);
                    addStage++;
                    break;
                case 2:
                    addEmbed.clearFields();
                    addEmbed.addField(new MessageEmbed.Field("Title", client.getTitle(), false));
                    addEmbed.addField(new MessageEmbed.Field("Description", fullMessage, false));
                    addEmbed.addField(new MessageEmbed.Field("Budget", "Please type in the budget", false));
                    client.setDescription(fullMessage);
                    addStage++;
                    break;
                case 3:
                    addEmbed.clearFields();
                    addEmbed.addField(new MessageEmbed.Field("Title", client.getTitle(), false));
                    addEmbed.addField(new MessageEmbed.Field("Description", client.getDescription(), false));
                    addEmbed.addField(new MessageEmbed.Field("Budget", fullMessage, false));

                    client.setBudget(fullMessage);

                    JSONObject newClientInfo = new JSONObject();

                    newClientInfo.put("MCMPostLink", client.getUrl());
                    newClientInfo.put("Title", client.getTitle());
                    newClientInfo.put("PluginDescription", client.getDescription());
                    newClientInfo.put("Budget", client.getBudget());

                    try{
                        JSONParser parser = new JSONParser();
                        Object o = parser.parse(new FileReader(System.getProperty("user.home") + "/documents/self-bot/clients.json"));
                        JSONObject object = (JSONObject) o;

                        object.put(client.getDiscordID(), newClientInfo);

                        FileWriter fw = new FileWriter(System.getProperty("user.home") + "/documents/self-bot/clients.json", false);
                        fw.write(object.toJSONString());
                        fw.flush();

                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }

                    addStage = 0;
                    break;
            }
            mc.sendMessage(addEmbed.build()).queue();
        }catch (IllegalArgumentException ie){
            sendError("The text cannot be longer than 1024 characters (" + fullMessage.length() + " chars), please try again", mc);
            addStage = 0;
        }
    }
}
