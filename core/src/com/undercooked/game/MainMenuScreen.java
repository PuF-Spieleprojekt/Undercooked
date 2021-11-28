package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.GL20;

public class MainMenuScreen implements Screen {

    final Undercooked game;
    OrthographicCamera camera;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;

    public MainMenuScreen(final Undercooked gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("AgentOrange.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 35;
        fontParameter.borderWidth = 2;
        fontParameter.borderColor = Color.FIREBRICK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

//
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(54/255f, 84/255f, 120/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        ScreenUtils.clear(5, 0.7, 0.9, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        font.draw(game.batch, "Undercooked!", 200, 300);
//        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
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
    }
}