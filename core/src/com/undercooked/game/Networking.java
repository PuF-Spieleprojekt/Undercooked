package com.undercooked.game;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.ChannelMessageAck;
import com.heroiclabs.nakama.ChannelType;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.MatchList;
import com.heroiclabs.nakama.api.StorageObject;
import com.heroiclabs.nakama.api.StorageObjectAcks;
import com.heroiclabs.nakama.api.StorageObjects;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class Networking {
    private static String hostUrl = "5.45.102.57";
    private static String serverKey = "defaultkey";


    private String id;


    private final DefaultClient client = new DefaultClient(serverKey, hostUrl, 7349, false);
    private ExecutorService executor;
    private ExecutorService socketExecutor;
    private Session session;
    private SocketClient socket;
    private Match match;
    private String roomName;
    private String matchID = "";
    private String extractedMatchID = "";
    private String messageID = "";
    private Channel channel;
    private String receivedData = "";
    private Boolean authenticationSuccessful;
    private Map<String, String> userData = new HashMap<>();
    private Map<String, String> playerData = new HashMap<>();
    private Map<String, String> timerData = new HashMap<>();
    private Map<String, String> ingredientData = new HashMap<>();
    private Map<String, String> createIngredientCommand = new HashMap<>();
    private Map<String, String> plateData = new HashMap<>();

    public Boolean joinedMatch = false;


    //TODO: replace with get userData()
    public String getUsername(){
        return session.getUsername();
    }

/*++++++++++++++++++++++++++++++++++
* ++++++++++++++++++++++++++++++++++
* USER-MANAGEMENT
* +++++++++++++++++++++++++++++++++
* +++++++++++++++++++++++++++++++++*/

    public boolean register(String email, String password, String username) throws ExecutionException, InterruptedException {
        executor = Executors.newSingleThreadExecutor();
        ListenableFuture<Session> registration = client.authenticateEmail(email, password, username);

        Futures.addCallback(registration, new FutureCallback<Session>() {
            @Override
            public void onSuccess(@NullableDecl Session result) {
                session = result;
                authenticationSuccessful = true;
                System.out.println("Registration successful!");
                System.out.println("Authentication successful. AuthToken created: " + session.getAuthToken());
                try {
                    createItemsCollection();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                executor.shutdown();
            }

            @Override
            public void onFailure(Throwable t) {
                authenticationSuccessful = false;
                System.out.println("Registration failed " + t.getMessage());
                executor.shutdown();
            }
        }, executor);


        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        if (authenticationSuccessful) {
            return true;
        }
        return false;
    }

    public boolean login(String email, String password) throws ExecutionException, InterruptedException {
        executor = Executors.newSingleThreadExecutor();
        ListenableFuture<Session> emailLogin = client.authenticateEmail(email, password, false);

        Futures.addCallback(emailLogin, new FutureCallback<Session>() {
            @Override
            public void onSuccess(@NullableDecl Session result) {
                session = result;
                authenticationSuccessful = true;
                System.out.println("Login successful!");
                System.out.println("Authentication successful. AuthToken created: " + session.getAuthToken());
                executor.shutdown();
            }

            @Override
            public void onFailure(Throwable t) {
                authenticationSuccessful = false;
                System.out.println("Login failed " + t.getMessage());
                executor.shutdown();
            }
        }, executor);


        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        if (authenticationSuccessful) {
            return true;
        } else {
            return false;
        }
    }

    public void createSocket() throws ExecutionException, InterruptedException {
        socketExecutor = Executors.newSingleThreadExecutor();
        socket = client.createSocket();
        ListenableFuture<Session> socketConnection = socket.connect(session, listener);

        Futures.addCallback(socketConnection, new FutureCallback<Session>() {
            @Override
            public void onSuccess(@NullableDecl Session result) {
                session = result;
                //joinChat(socket);
                System.out.println("Socket Creation successful!");
                //Leave executor running so socket stays active
                //executor.shutdown();
            }

            @Override
            public void onFailure(Throwable t) {
                authenticationSuccessful = false;
                System.out.println("Socket Creation failed! " + t.getMessage());
                executor.shutdown();
            }
        }, socketExecutor);
    }




    /*++++++++++++++++++++++++++++++++++
     * ++++++++++++++++++++++++++++++++++
     * MATCH-MAKING
     * +++++++++++++++++++++++++++++++++
     * +++++++++++++++++++++++++++++++++*/

    public Boolean makeMatch() throws ExecutionException, InterruptedException {
        if (!matchID.isEmpty()) {
            socket.leaveMatch(match.getMatchId()).get();
            System.out.println("Match still ongoing. First leave match..");
            System.out.println("Match " + matchID + "left.");
            matchID = "";
            extractedMatchID = "";
            return false;
        } else {
            if (!session.getAuthToken().isEmpty()) {
                //TODO: Add Callbacklistener for Match creation
                match = socket.createMatch().get();
                System.out.println("Match created with ID: " + match.getMatchId());
                return true;
            }
            return false;
        }

    }


    public boolean joinMatch() throws ExecutionException, InterruptedException {
        MatchList matchlist = findMatch();

        if (matchlist.getMatchesCount() != 0) {
            match = socket.joinMatch(matchlist.getMatches(0).getMatchId()).get();
            for (UserPresence presence : match.getPresences()) {
                System.out.format("User id %s name %s.", presence.getUserId(), presence.getUsername());
            }
            System.out.println("Match " + matchlist.getMatches(0).getMatchId() + " joined successfully");
            joinedMatch = true;
            return true;
        } else {
            System.out.println("Can't connect hence no game is active..");
            return false;
        }

    }

    public MatchList findMatch() throws ExecutionException, InterruptedException {
        MatchList matchlist = client.listMatches(session, 1).get();
        System.out.println("Matchlist created. Authentication via AuthToken: " + session.getAuthToken());
        return matchlist;
        //System.out.println(matchlist.getMatchesCount());
    }




    /*++++++++++++++++++++++++++++++++++
     * ++++++++++++++++++++++++++++++++++
     * DATA-SYNC
     * +++++++++++++++++++++++++++++++++
     * +++++++++++++++++++++++++++++++++*/

    public void sendPlayerData(String direction, String hitboxX, String hitboxY, String userID) {
        if (!match.getMatchId().isEmpty()) {
            long opCode = 1;
            Map<String,String> dataString = new HashMap<>();

            dataString.put("hitboxY", hitboxY);
            dataString.put("hitboxX", hitboxX);
            dataString.put("direction", direction);
            dataString.put("userID", userID);

            String dataJson = new Gson().toJson(dataString);
            byte[] byteData = dataJson.getBytes();
            socket.sendMatchData(match.getMatchId(), opCode, byteData);
        }
    }

    public void sendTimerData(String timerPurpose, String seconds){
        if (!match.getMatchId().isEmpty()) {
            long opCode = 2;

            Map<String,String> dataString = new HashMap<>();

            dataString.put("timerPurpose", timerPurpose);
            dataString.put("seconds", seconds);

            String dataJson = new Gson().toJson(dataString);
            byte[] byteData = dataJson.getBytes();
            socket.sendMatchData(match.getMatchId(), opCode, byteData);
        }
    }

    public void sendIngredientData(String create, String texture, String hitboxX, String hitboxY, String ownerID){
        if (!match.getMatchId().isEmpty()) {
            long opCode = 3;

            Map<String,String> dataString = new HashMap<>();

            dataString.put("holding", create);
            dataString.put("texture", texture);
            dataString.put("hitboxX", hitboxX);
            dataString.put("hitboxY", hitboxY);
            dataString.put("ownerID", ownerID);

            String dataJson = new Gson().toJson(dataString);
            byte[] byteData = dataJson.getBytes();
            socket.sendMatchData(match.getMatchId(), opCode, byteData);
        }
    }

    public void sendPlateData(String hasPlate){
        if (!match.getMatchId().isEmpty()) {
            long opCode = 5;

            Map<String,String> dataString = new HashMap<>();

            dataString.put("hasPlate", hasPlate);


            String dataJson = new Gson().toJson(dataString);
            byte[] byteData = dataJson.getBytes();
            socket.sendMatchData(match.getMatchId(), opCode, byteData);
        }
    }

    public void createIngredientCommand(String create){
        if (!match.getMatchId().isEmpty()) {
            long opCode = 4;

            Map<String,String> dataString = new HashMap<>();

            dataString.put("create", create);

            String dataJson = new Gson().toJson(dataString);
            byte[] byteData = dataJson.getBytes();
            socket.sendMatchData(match.getMatchId(), opCode, byteData);
        }
    }

    public Map<String, String> getUserdata(){
        userData.put("username", session.getUsername());
        userData.put("userID", session.getUserId());

        return userData;
    }

    public Map<String, String> getPlayerData() {
        return playerData;
    }

    public Map<String, String> getTimerData() {
        return timerData;
    }

    public Map<String, String> getIngredientData() {
        return ingredientData;
    }

    public Map<String, String> getCreateIngredientCommand() {
        return createIngredientCommand;
    }

    public Map<String, String> getPlateData(){ return plateData; }


    final SocketListener listener = new AbstractSocketListener() {
        @Override
        public void onMatchData(final MatchData matchData) {
            //As soon there is match data, a player has joined the Match
            joinedMatch = true;
            //System.out.println("Listener: ");
            //System.out.println(new String(matchData.getData()));
            // received Data
            receivedData = new String(matchData.getData());

            // Data gets formatted so it can be used further on
            switch (String.valueOf(matchData.getOpCode())){
                case "1" :
                    playerData = retrieveNetworkData(receivedData);
                    break;
                case "2" :
                    timerData = retrieveNetworkData(receivedData);
                    break;
                case "3" :
                    ingredientData = retrieveNetworkData(receivedData);
                    break;
                case "4" :
                    createIngredientCommand = retrieveNetworkData(receivedData);
                    break;
                case "5" :
                    plateData = retrieveNetworkData(receivedData);
            }

        }
    };


    public Map<String,String> retrieveNetworkData(String receivedData) {
        String[] newData = receivedData.split(",");
        Map<String, String> dataMap = new HashMap<String, String>();

        for (int i = 0; i < newData.length; i++) {
            String key = newData[i].split(":")[0].replaceAll(",", "").replaceAll("\\{", "").replaceAll("\"", "").replaceAll("}", "");
            String value = newData[i].split(":")[1].replaceAll(",", "").replaceAll("\\{", "").replaceAll("\"", "").replaceAll("}", "");
            dataMap.put(key,value);
        }
        return dataMap;
    }
    public void resetPlayerData(){
        playerData.clear();
    }
    /*++++++++++++++++++++++++++++++++++
     * ++++++++++++++++++++++++++++++++++
     * STORAGE-MANAGEMENT
     * +++++++++++++++++++++++++++++++++
     * +++++++++++++++++++++++++++++++++*/


    //creates collection for every user, that can be filled with items and highscores
    public void createItemsCollection() throws ExecutionException, InterruptedException {
        Map<String, List<String>> usableItems = new HashMap<>();
        Map<String, List<String>> highscores = new HashMap<>();
        //TODO: Fill with content
        usableItems.put("Skins",  Arrays.asList("green", "blue"));
        highscores.put("Highscores", Arrays.asList("0000"));

        String usableItemsJson = new Gson().toJson(usableItems);
        String highscoresJson = new Gson().toJson(highscores);

        StorageObjectWrite saveGameObject = new StorageObjectWrite("items", "item", usableItemsJson, PermissionRead.OWNER_READ, PermissionWrite.OWNER_WRITE);
        StorageObjectWrite statsObject = new StorageObjectWrite("stats", "scores", highscoresJson, PermissionRead.PUBLIC_READ, PermissionWrite.OWNER_WRITE);

        StorageObjectAcks acks = client.writeStorageObjects(session, saveGameObject, statsObject).get();
        System.out.format("Stored objects %s", acks.getAcksList());
    }



    public void updateItemCollectionData(String collectionName, String keyName, String tableKey, List<String> newData ) throws ExecutionException, InterruptedException {
        Map<String, List<String>> data = new HashMap<>();

        data.put("tableKey", newData);
        String dataJson = new Gson().toJson(data);

        StorageObjectWrite dataObject = new StorageObjectWrite(collectionName, keyName, dataJson, PermissionRead.PUBLIC_READ, PermissionWrite.OWNER_WRITE);
        StorageObjectAcks acks = client.writeStorageObjects(session, dataObject).get();
    }



    //gets data from server and converts it to a List<String>, so it can be displayed
    public List<String> retrieveStorageData(String storageName, String key) throws ExecutionException, InterruptedException {
        List<String> data = null;
        StorageObjectId objectId = new StorageObjectId(storageName);

        objectId.setKey(key);
        objectId.setUserId(session.getUserId());
        //TODO: Implement proper logging maybe?
        System.out.println("Retrieving storage data..");
        StorageObjects objects = client.readStorageObjects(session, objectId).get();
        System.out.println(objects.getObjectsCount());

        for(StorageObject object : objects.getObjectsList()){
            Map<String, List<String>> parsedObj = new Gson().fromJson(object.getValue(), new TypeToken<Map<String, List<String>>>(){}.getType());
            String temp = parsedObj.values().toString().replaceAll("[\\[\\](){}]","");
            data = new ArrayList<String>(Arrays.asList(temp.split(",")));
        }
        return data;
    }


    public Networking() { };

}


