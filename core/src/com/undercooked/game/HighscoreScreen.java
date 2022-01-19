package com.undercooked.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class HighscoreScreen extends ControlScreen implements Screen {
    final Undercooked game;
    final Networking net;

    public HighscoreScreen(Undercooked game, Networking net){
        super();
        this.game = game;
        this.net = net;
    }
    @Override
    public void show(){
        super.show();

        super.backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game, net));
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
