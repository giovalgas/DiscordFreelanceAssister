package me.giodev.misc;

public class ClientObject {

    String url;
    String discordID;
    String title;
    String description;
    String budget;

    public ClientObject(String discordID, String url){
        this.discordID = discordID;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getDiscordID() {
        return discordID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public void reset(){
        this.title = "";
        this.description = "";
        this.budget = "";
    }

}
