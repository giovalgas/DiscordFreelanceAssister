package me.giodev;

import me.giodev.listeners.MessageReceivedListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class FreelanceAssister {

    private static JDA jda;

    public static JDA getJDA(){
        return jda;
    }

    public static void main(String args[]) throws Exception {

        jda = JDABuilder.createDefault(getToken()).build();
        jda.addEventListener(new MessageReceivedListener());

        jda.awaitReady().getGuildById("580481568880197632").getTextChannelById("753487352495734785").sendMessage("bom dia").queue();

    }

    private static String getToken(){
        JSONParser parser = new JSONParser();
        try {
            Object o = parser.parse(new FileReader(System.getProperty("user.home") + "/documents/self-bot/token.json"));
            JSONObject object = (JSONObject) o;

            return String.valueOf(object.get("token"));

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
