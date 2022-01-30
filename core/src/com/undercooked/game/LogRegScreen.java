package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LogRegScreen extends ControlScreen implements Screen {
    final Undercooked game;
    final Networking net;



    public LogRegScreen(Undercooked game, Networking net) {
        super();
        this.game = game;
        this.net = net;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        super.show();
        super.backButton.remove();
        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        super.stage.addActor(table);




        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //game.setScreen(new MainMenuScreen(game));
            }
        });

        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));
        TextButton register = new TextButton("Register", skin);
        TextButton login = new TextButton("Login", skin);
        TextButton justPlay = new TextButton("Just PLay", skin);
        register.setPosition(230, 180);
        login.setPosition(230, 130);
        super.stage.addActor(register);
        super.stage.addActor(login);

        table.add(register);
        table.row();
        table.add(login);
        table.row();
        table.add(justPlay);


        register.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RegisterScreen(game, net));
            }
        });

        login.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                 game.setScreen(new LoginScreen(game, net));

            }
        });
        justPlay.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, net, false, true));
            }
        });
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {

        stage.dispose();
    }
}
