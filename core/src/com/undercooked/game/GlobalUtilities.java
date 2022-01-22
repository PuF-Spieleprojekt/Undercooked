package com.undercooked.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GlobalUtilities {

    //Highscore management:

    public static int highscore = 0;
    public static List<String> highscoreList = new ArrayList<String>();
    public static List<String> highscoreListUpdated = new ArrayList<String>();

    public static void resetHighscore(){
        highscore = 0;
    }

    public static List<String> sortHighscoreList(){
        List<Integer> tempList = new ArrayList<>();
        highscoreListUpdated.clear();
        for(String score : highscoreList){
           int temp = Integer.parseInt(score.replaceAll("\\s+",""));
           tempList.add(temp);
        }

        Collections.sort(tempList);
        for(int element : tempList){
            highscoreListUpdated.add(String.valueOf(element));
        }
        Collections.reverse(highscoreListUpdated);

        if(highscoreList.size()> 10){
            highscoreList.remove(11);
        }

        return highscoreListUpdated;
    }

    //Skins:

    public static String skinAsString;

}
