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

public class RoundScreen extends ControlScreen implements Screen {

    final Undercooked game;
    final Networking net;

    public RoundScreen (Undercooked game, Networking net) {
        super();
        this.game = game;
        this.net = net;
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
        Label round_over = new Label("Round over", ls);
        table.add(round_over);

        TextButton playAgain = new TextButton("Play again", skin);
        playAgain.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                    game.setScreen(new GameScreen(game, net, true));

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

