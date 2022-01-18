package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.concurrent.ExecutionException;

public class MultiplayerScreen extends ControlScreen implements Screen {

    final Undercooked game;
    final Networking net;
    Boolean foundGame = false;

    public MultiplayerScreen(Undercooked game, Networking net){
        super();
        this.game = game;
        this.net = net;
    }
    @Override
    public void show() {
        super.show();
        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));


        final Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);

//        table.setDebug(true);
        super.stage.addActor(table);


        final Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = game.font;
        TextButton createGame = new TextButton("Create Game", skin);
        final TextButton findGame = new TextButton("Find Game", skin);

        table.columnDefaults(1);
        table.add(createGame);
        table.row().pad(5, 0, 5, 0);
        table.add(findGame);
        table.row().pad(5, 0, 5, 0);


        createGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if(net.makeMatch()){
                       // game.setScreen(new GameScreen(game, net, true));
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        findGame.addListener(new ChangeListener() {
             @Override
             public void changed(ChangeEvent event, Actor actor) {
                 try {
                     if(net.findMatch().getMatchesCount() !=0 && foundGame == false){
                         findGame.setText("Join Match");
                         foundGame = true;
                     } else if(foundGame == true){
                         if(net.joinMatch()){
                             game.setScreen(new GameScreen(game, net, true));
                         }
                     }

                    /* if(net.joinMatch()){
                         game.setScreen(new GameScreen(game, net, true));
                     }*/
                 } catch (ExecutionException e) {
                     e.printStackTrace();
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 }
             }
        });
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

