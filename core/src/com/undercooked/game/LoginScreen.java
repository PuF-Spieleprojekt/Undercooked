package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.concurrent.ExecutionException;

public class LoginScreen extends ControlScreen implements Screen {

    final Undercooked game;
    final Networking net;

    public LoginScreen(Undercooked game, Networking net){
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
        final Label usernameLabel = new Label("E-Mail", ls);
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

        super.backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LogRegScreen(game, net));
            }
        });

        submit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                try {
                    if(net.login(usernameField.getText(), passwordField.getText())){
                        net.createSocket();
                        game.setScreen(new MainMenuScreen(game, net));
                    } else {
                        usernameLabel.setText("Login failed, please try again");
                    }
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
