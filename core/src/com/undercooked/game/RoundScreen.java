package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.concurrent.ExecutionException;

import grpc.gateway.protoc_gen_openapiv2.options.Openapiv2;

public class RoundScreen extends ControlScreen implements Screen {

    final Undercooked game;
    final Networking net;
    final Boolean multiplayer;

    public RoundScreen (Undercooked game, Networking net, Boolean multiplayer) throws ExecutionException, InterruptedException {
        super();
        this.game = game;
        this.net = net;
        this.multiplayer = multiplayer;

        //as soon as this screen gets built the game is finished and the highscore is set
        //the highscore is added to highscorelist and updated
        GlobalUtilities.highscoreList = net.retrieveStorageData("stats", "scores");
        GlobalUtilities.highscoreList.add(String.valueOf(GlobalUtilities.highscore));
        net.updateItemCollectionData("stats", "scores", "Highscores", GlobalUtilities.sortHighscoreList());

        // get data for played games and won games and update them according to the game result.
        GlobalUtilities.gamesList = net.retrieveStorageData("stats", "games");
        GlobalUtilities.playedGames = Integer.parseInt(GlobalUtilities.gamesList.get(0));
        GlobalUtilities.playedGames++;
        GlobalUtilities.gamesList.set(0,String.valueOf(GlobalUtilities.playedGames));

        if(GlobalUtilities.highscore > GlobalUtilities.highscorePlayer2){
            GlobalUtilities.wonGames = Integer.parseInt(GlobalUtilities.gamesList.get(1));
            GlobalUtilities.wonGames++;
            GlobalUtilities.gamesList.set(1,String.valueOf(GlobalUtilities.wonGames));
        }

        net.updateItemCollectionData("stats", "games", "Played Games", GlobalUtilities.gamesList);

    }

    public void show() {
        super.show();

        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = game.font;

        Label round_over = new Label("", ls);

        Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);
//        table.setDebug(true);
        super.stage.addActor(table);


        if(!multiplayer){
            round_over.setText("Round over. Your score: " + GlobalUtilities.highscore);
        } else {
            if(GlobalUtilities.highscore > GlobalUtilities.highscorePlayer2){
                round_over.setText("You WON!!!" + "\n" + "Your score is: " + GlobalUtilities.highscore);
            } else if (GlobalUtilities.highscore == GlobalUtilities.highscorePlayer2) {
                round_over.setText("Draw. At least you didn't loose." + "\n" + "Your score is: " + GlobalUtilities.highscore);
            } else {
                round_over.setText("NOOOOO! You lost.." + "\n" + "We won't tell you your score. It's a mess!");
            }
        }
        table.add(round_over);

        TextButton playAgain = new TextButton("Back to Main Menu", skin);
        playAgain.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                    //when leaving this screen highscore gets reset
                    GlobalUtilities.resetHighscore();
                try {
                    game.setScreen(new MainMenuScreen(game, net));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        table.row();
        table.add(playAgain);

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        // Closes the window using ecs button.
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }
}

