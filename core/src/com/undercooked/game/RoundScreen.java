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

public class RoundScreen extends ControlScreen implements Screen {

    final Undercooked game;
    final Networking net;

    public RoundScreen (Undercooked game, Networking net) throws ExecutionException, InterruptedException {
        super();
        this.game = game;
        this.net = net;

        //as soon as this screen gets built the game is finished and the highscore is set
        //the highscore is added to highscorelist and updated
        GlobalUtilities.highscoreList = net.retrieveStorageData("stats", "scores");
        GlobalUtilities.highscoreList.add(String.valueOf(GlobalUtilities.highscore));
        net.updateItemCollectionData("stats", "scores", "Highscores", GlobalUtilities.sortHighscoreList());

    }

    public void show() {
        super.show();

        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));


        Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);
//        table.setDebug(true);
        super.stage.addActor(table);
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = game.font;
        Label round_over = new Label("Round over. Your score: " + GlobalUtilities.highscore, ls);
        table.add(round_over);

        TextButton playAgain = new TextButton("Play again", skin);
        playAgain.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                    //when leaving this screen highscore gets reset
                    GlobalUtilities.resetHighscore();
                    game.setScreen(new MainMenuScreen(game, net));

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

