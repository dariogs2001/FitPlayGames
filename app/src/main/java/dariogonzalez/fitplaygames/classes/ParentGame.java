package dariogonzalez.fitplaygames.classes;

import android.graphics.drawable.Drawable;

import com.parse.ParseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Dario on 8/15/2015.
 */
public abstract class ParentGame {
    private ParseObject gameObject; // The parseObject from the DB of the game
    private int gameType; // This comes from the game type constants class
    private String name; // Name of the game
    private String userGameName; // Name that the user gives the game
    private String description; // The game description
    private int stepsGoal; // Step goal for the game
    private enum GameType {
        ACTIVE, PENDING, FINISHED
        }
    private Drawable icon; // The game icon
    private Date startDate; // The user set startDate/time
    private Date endDate; // The system set endDate/time
    private ArrayList<String> playerIds; // A list of all the player ids of the game. This should be > 2 for game to start
    private ArrayList<String> activePlayers; // A list of all the activate players of the game. This could be 1 or more


    // This method will return a default game name based on the name of the game and possibly the date or something
    public String getDefaultGameName() {

        return "";
    }

    // This method will do the sending of push notifications to the other players (playerIds)
    // This could be for inviting, starting, ending etc.
    public void sendPushNotiication(String message, Drawable icon, ArrayList<String> userIds) {

    }

    // This method will change the game status to be "started"
    public void startGame() {

    }

    // This method will change the game status to be "ended" and will be called if the user cancels a game as well or if there are not more than one players
    public void endGame() {

    }

    // This method will have the logic needed to send invitations and will be calling sendPushNotification
    public void sendInvitation() {

    }

    // This method will do the logic to update the game per user and will be called from checkPlayerStatus
    public void updateGame() {

    }

    // This method will call checkUserStatus after it gets the Challenge data from parse
    private void initGame() {

    }

    // This method will check the game status and the players status when they open up the game. It should be called for every game that the user is involved with.
    // It will hold all the logic to know what it needs to do every time a user opens up the app.
    public void checkUserStatus() {

    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserGameName() {
        return userGameName;
    }

    public void setUserGameName(String userGameName) {
        this.userGameName = userGameName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStepsGoal() {
        return stepsGoal;
    }

    public void setStepsGoal(int stepsGoal) {
        this.stepsGoal = stepsGoal;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<String> playerIds) {
        this.playerIds = playerIds;
    }

    public ArrayList<String> getActivePlayers() {
        return activePlayers;
    }

    public void setActivePlayers(ArrayList<String> activePlayers) {
        this.activePlayers = activePlayers;
    }
}
