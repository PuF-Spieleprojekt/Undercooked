package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.awt.Color;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;

public class LoginScreen extends ControlScreen implements Screen {

    final Undercooked game;
    public Networking net;
    public LoginScreen(Undercooked game) throws InterruptedException, ExecutionException, MalformedURLException {
        super();
        this.game = game;
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
        Label usernameLabel = new Label("E-Mail", ls);
        Label passwordLabel = new Label("Password", ls);
        final TextField usernameField = new TextField("", skin); // User Input
        final TextField passwordField = new TextField("", skin); // user Input
        TextButton submit = new TextButton("OK", skin);

        table.add(usernameLabel);
        table.row().pad(5, 0, 5, 0);
        table.add(usernameField);
        table.row().pad(5, 0, 5, 0);
        table.add(passwordLabel);
        table.row().pad(5, 0, 5, 0);
        table.add(passwordField);
        table.row().pad(5, 0, 5, 0);
        table.add(submit);

        submit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    net = new Networking(usernameField.getText(), passwordField.getText());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(Networking.authenticationSuccessful){
                    game.setScreen(new MainMenuScreen(game, net));

                }else{
                    table.add(new Label(Networking.errorMessage, ls));
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
