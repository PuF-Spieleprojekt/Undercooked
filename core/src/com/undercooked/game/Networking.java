package com.undercooked.game;


import java.net.MalformedURLException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.net.Socket;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.rpc.context.AttributeContext;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Channel;
import com.heroiclabs.nakama.ChannelMessageAck;
import com.heroiclabs.nakama.ChannelType;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.Account;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.sun.jmx.remote.internal.ClientListenerInfo;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import grpc.gateway.protoc_gen_openapiv2.options.Openapiv2;

public class Networking {
    private static String hostUrl = "5.45.102.57";
    private static String serverKey = "defaultkey";


    private String id;


    private final DefaultClient client = new DefaultClient(serverKey, hostUrl, 7349, false);
    private ExecutorService executor;
    private Session session;
    private Session socketSession;
    private SocketClient socket;
    private Match match;
    private String roomName;
    private String matchID = "";
    private String extractedMatchID = "";
    private String messageID = "";
    private Channel channel;
    private String receivedData = "";
    private String[] formatedData = new String[1];
    public Boolean joinedMatch = false;


    public boolean login(String email, String password) throws ExecutionException, InterruptedException {
        session = client.authenticateEmail(email, password).get();
        System.out.println("Authentication successful. AuthToken created: " + session.getAuthToken());

        if(!session.getAuthToken().isEmpty()){
            System.out.println(session.getAuthToken());
            return true;
        }else{
            return false;
        }

    }

    public void createSocket() throws ExecutionException, InterruptedException {
        if(!session.getAuthToken().isEmpty()){
            socket = client.createSocket();
            socketSession= socket.connect(session, listener).get();
            joinChat(socket);
        }

    }

    public Boolean makeMatch() throws ExecutionException, InterruptedException {
        if(!matchID.isEmpty()){
            socket.leaveMatch(match.getMatchId()).get();
            System.out.println("Match still ongoing. First leave match..");
            System.out.println("Match " + matchID + "left.");
            matchID = "";
            extractedMatchID = "";
            return false;

               /* socket.removeChatMessage(channel.getId(), messageID);
                System.out.println("Old MatchId was removed.");
                messageID = "";*/
        }else{
            if(!socketSession.getAuthToken().isEmpty()){
                match = socket.createMatch().get();
                matchID = "{\"matchID\":\""+ match.getMatchId() +"\"}";
                ChannelMessageAck sendAck = socket.writeChatMessage(channel.getId(), matchID).get();
                System.out.println("Match created with ID: " + match.getMatchId());
                return true;
            }
            return false;
        }

    }

    public void resendMatchData() throws ExecutionException, InterruptedException {
        ChannelMessageAck sendAck = socket.writeChatMessage(channel.getId(), matchID).get();
    }

    public void joinMatch() throws ExecutionException, InterruptedException {
        if(!extractedMatchID.isEmpty()){
            match = socket.joinMatch(extractedMatchID).get();
            for (UserPresence presence : match.getPresences()) {
                System.out.format("User id %s name %s.", presence.getUserId(), presence.getUsername());
            }


        } else {
            System.out.println("Can't connect hence no game is active..");
        }

    }

    public void sendMatchData(String texture, String hitboxX, String hitboxY) {
        if (!match.getMatchId().isEmpty()) {
            long opCode = 1;
            String data = "{\"texture\" : \""+ texture +"\", \" hitboxX \" : \""+ hitboxX +"\", \" hitboxY \" : \""+ hitboxY +"\" }" ;
            byte[] byteData = data.getBytes();
            socket.sendMatchData(match.getMatchId(), opCode, byteData);


        }
    }

    final SocketListener listener = new AbstractSocketListener() {
        @Override
        public void onMatchData(final MatchData matchData) {

            //System.out.format("Received match data %s with opcode %d", matchData.getData(), matchData.getOpCode());
            System.out.println(new String(matchData.getData()));
            //System.out.println(matchData.toString());
            receivedData = new String(matchData.getData());
            formatedData = retrieveNetworkData(receivedData);
            joinedMatch = true;

        }
        @Override
        public void onChannelMessage(final ChannelMessage message) {

            messageID = message.getMessageId();
            extractedMatchID = message.getContent().split(":")[1].replaceAll("\"", "").replaceAll("}", "");
            System.out.println(extractedMatchID);
            System.out.format("Received a message on channel %s", message.getChannelId());
            System.out.format("Message content: %s", message.getContent());
        }
    };

    public void joinChat(SocketClient socket) throws ExecutionException, InterruptedException {
         roomName = "GameRoom";
         channel = socket.joinChat(roomName, ChannelType.ROOM).get();
    }

    public String[] getMatchdata(){
        return formatedData;
    }

    public String[] retrieveNetworkData(String receivedData){
        String[] newData = receivedData.split(",");
        String[] trimmedData = new String[newData.length];
        for(int i = 0; i < newData.length; i++){
            trimmedData[i] = newData[i].split(":")[1].replaceAll(",", "").replaceAll("\\{", "").replaceAll("\"", "").replaceAll("}", "");
        }
        return trimmedData;
    }




    public Networking() {

    }

   /* public Future<String> test(String email, String password) throws ExecutionException, InterruptedException {
        executor = Executors.newSingleThreadExecutor();
       Future <Session> session = client.authenticateEmail(email, password);
       String authToken = session.get().getAuthToken();

       if(session.isDone()){
           return response;
       }

    }*/



   /* public void loginWithEmail(String email, String password){

        executor = Executors.newSingleThreadExecutor();
        try {
            Futures.addCallback(client.authenticateEmail(email, password), new FutureCallback<Session>() {
                @Override
                public void onSuccess(final Session result) {
                    System.out.println("got session: " + result.getAuthToken());
                    authenticationSuccessful = true;
                    SocketClient socketClient = client.createSocket();
                  *//*  try {
                        //createSocketListener(result, socketClient);
                        System.out.println("Socket Created");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*//*
                    *//*socketClient = client.createSocket();


                    Futures.addCallback(socketClient.connect(result, listener), new FutureCallback<Session>() {
                        @Override
                        public void onSuccess(Session result) {
                            authenticationSuccessful = true;
                            session = result;
                            System.out.println( "Socket Created " + result.toString());
                            System.out.println(listener.toString());


                        }
                        @Override
                        public void onFailure(Throwable t) {
                            System.out.println( "Socket Creation failed " + t.toString());
                        }
                    }, executor);*//*
                    //executor.shutdown();
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    System.out.println(throwable.getMessage());
                    //executor.shutdown();
                }
            }, executor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }*/




  /*  public void createSocketListener(Session session, SocketClient socketClient) throws ExecutionException, InterruptedException {

        socketClient.connect(session, listener).get();
        createMatch(socketClient);



    }*/


    public void createMatch(final SocketClient socketClient) throws ExecutionException, InterruptedException {

        try{

            Futures.addCallback(socketClient.createMatch(), new FutureCallback<Match>() {

                @Override
                public void onSuccess(@NullableDecl Match result) {
                    id = result.getMatchId();
                    System.out.println("Match created with ID: " + result.getMatchId());
                    try {
                        socketClient.joinMatch(id).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    //sendData(socketClient);

                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("Match couldn't get created." + t.getMessage());

                }
            }, executor);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        //match = socketClient.createMatch().get();
        //System.out.println("Created match with ID %s." + match.getMatchId());
    }





    }

/*    public void authenticateWithEmail(String email, String password){
        final ListenableFuture<Session> authFuture = client.authenticateEmail(email, password);


        AsyncFunction<Session, Account> accountFunction = new AsyncFunction<Session, Account>() {
            public ListenableFuture<Account> apply(Session session) {
                return client.getAccount(session);
            }
        };

        ListenableFuture<Account> accountFuture = Futures.transformAsync(authFuture, accountFunction, executor);
        Futures.addCallback(accountFuture, new FutureCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                System.out.println("Got account: " + account.getUser().getId());
                authenticationSuccessful = true;
                executor.shutdown();
            }

            @Override
            public void onFailure(Throwable e) {
                System.out.println(e.getMessage());
                executor.shutdown();
            }

        }, executor);

        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            errorMessage = e.getMessage();
            System.out.println(e.getMessage());
        }
    }


    public static void createSocketSession(String email, String password){
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Futures.addCallback(client.authenticateEmail(email, password), new FutureCallback<Session>() {
                @Override
                public void onSuccess(final Session result) {
                    System.out.println("got session: " + result.getAuthToken());
                    SocketClient socketClient = client.createSocket();

                    Futures.addCallback(socketClient.connect(result, new AbstractSocketListener() {}), new FutureCallback<Session>() {
                        @Override
                        public void onSuccess(Session result) {
                            authenticationSuccessful = true;
                            System.out.println( "Socket Created " + result.toString());
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            System.out.println( "Socket Creation failed " + t.toString());
                        }
                    }, executor);


                    //executor.shutdown();
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    System.out.println(throwable.getMessage());
                    //executor.shutdown();
                }
            }, executor);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }*/

