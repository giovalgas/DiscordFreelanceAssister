import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class FreelanceAssister {

    public static void main(String args[]) throws Exception {

         JDA jda = new JDABuilder(getToken()).build();

    }

    private static String getToken(){
        JSONParser parser = new JSONParser();
        try {
            Object o = parser.parse(new FileReader(System.getProperty("user.dir") + "/src/main/resources/token.json"));
            JSONObject object = (JSONObject) o;

            return String.valueOf(object.get("token"));

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
