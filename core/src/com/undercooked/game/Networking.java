package com.undercooked.game;


import java.net.MalformedURLException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.net.Socket;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.api.Account;
import com.sun.jmx.remote.internal.ClientListenerInfo;

public class Networking {
    private static String hostUrl = "192.168.111.75";
    private static String serverKey = "defaultkey";
    static Boolean authenticationSuccessful = false;
    static String errorMessage;
    private String email;
    private String password;
    private SocketClient socketClient;

    private  final DefaultClient client;
    final ExecutorService executor;

    public Networking(String email, String password) throws MalformedURLException, ExecutionException, InterruptedException {
        this.password = password;
        this.email = email;
        client = new DefaultClient(serverKey, hostUrl, 7349, false);
        executor = Executors.newSingleThreadExecutor();

        try {
            Futures.addCallback(client.authenticateEmail(email, password), new FutureCallback<Session>() {
                @Override
                public void onSuccess(final Session result) {
                    System.out.println("got session: " + result.getAuthToken());
                    socketClient = client.createSocket();

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

    }

    public void createMatch() throws ExecutionException, InterruptedException {
        Match match = socketClient.createMatch().get();
        System.out.println("Created match with ID %s." + match.getMatchId());
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

}