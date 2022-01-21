package com.undercooked.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenuScreen extends ControlScreen implements Screen {

    final Undercooked game;
    final Networking net;
    OrthographicCamera camera;

    public MainMenuScreen(final Undercooked gam, final Networking net) {

        super();
        game = gam;
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
        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));
        TextButton play = new TextButton("Play", skin);
        TextButton multiplayer = new TextButton("Multiplayer", skin);
        TextButton profile = new TextButton("Profile", skin);
        TextButton highscore = new TextButton("Highscore", skin);
        TextButton quit = new TextButton("Quit", skin);

        
        super.stage.addActor(play);

        Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);            // hardcoded position
//        table.setDebug(true);
        stage.addActor(table);
        table.add(play);
        table.row();
        table.add(multiplayer);
        table.row();
        table.add(profile);
        table.row();
        table.add(highscore);
        table.row();
        table.add(quit);

        super.backButton.addListener(new ChangeListener() {
            //TODO: Here should be a logout
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LoginScreen(game, net));
            }
        });

        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                    game.setScreen(new GameScreen(game, net, false, true));

            }
        });

        multiplayer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MultiplayerScreen(game, net));
            }
        });

        profile.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ProfileScreen(game, net));
            }
        });

        highscore.addListener(new ChangeListener(){

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new HighscoreScreen(game, net));
            }
        });

        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
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