package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RegisterScreen extends ControlScreen implements Screen {
    final Undercooked game;

    public RegisterScreen(Undercooked game) {
        super();
        this.game = game;
    }

    @Override
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
        Label usernameLabel = new Label("Username", ls);
        Label passwordLabel = new Label("Password", ls);
        TextField usernameField = new TextField("", skin); // User Input
        TextField passwordField = new TextField("", skin); // user Input
        TextButton submit = new TextButton("Submit", skin);

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
               // game.setScreen(new MainMenuScreen(game));
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
