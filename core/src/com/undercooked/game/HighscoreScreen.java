package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.concurrent.ExecutionException;

public class HighscoreScreen extends ControlScreen implements Screen {
    final Undercooked game;
    final Networking net;


    public HighscoreScreen(Undercooked game, Networking net){
        super();
        this.game = game;
        this.net = net;

        try {
            GlobalUtilities.highscoreList = net.retrieveStorageData("stats", "scores");
            GlobalUtilities.gamesList = net.retrieveStorageData("stats", "games");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void show(){
        super.show();

        super.backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    game.setScreen(new MainMenuScreen(game, net));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));

        super.label.setText("Statistics: ");
        final Table table = new Table();
        table.top();
        table.padTop(80);
        table.setFillParent(true);
        table.setY(-80);
//        table.setDebug(true);
        super.stage.addActor(table);
        table.add(new Label("Played Games: " + GlobalUtilities.gamesList.get(0), skin)).row();
        table.add(new Label("Won Games: " + GlobalUtilities.gamesList.get(0), skin)).row();
        table.add(new Label("Higscores: ", skin)).row();
        for(String score : GlobalUtilities.highscoreList){
            table.add(new Label(score,skin)).row();

        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);


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
