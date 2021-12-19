package com.undercooked.game;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import jdk.nashorn.internal.parser.JSONParser;


public class Networking {
    private static String hostUrl = "https://nakama.vegardit.com";
    private static URL serverUrl;
    private static String serverKey = "rg6RUkl8.!:";



    public Networking() throws MalformedURLException {
    }

    public static void createUserWithEmail(String email, String password){
        HttpURLConnection connection = null;
        String encodedServerKey = Base64.getEncoder().encodeToString(serverKey.getBytes());
        JSONObject jsonObject = new JSONObject();


        jsonObject.put("email", email);

        jsonObject.put("password", password);
        String jsonInputString = "{\"email\": \"test@mail.com\",\"password\":\"password\"}";
        System.out.println(jsonInputString);
        try{
            serverUrl = new URL("https://nakama.vegardit.com/v2/account/authenticate/email?create=true&username="+email);
            connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic "+encodedServerKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setDoOutput(true);



            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonInputString.getBytes("UTF-8"));
            outputStream.close();

            connection.connect();

            System.out.println(connection.getResponseCode());



            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response.toString());




        }catch (Exception e){
            System.out.println(e);

        }
    }

}