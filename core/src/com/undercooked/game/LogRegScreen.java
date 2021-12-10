package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class LogRegScreen extends ControlScreen implements Screen {
    final Undercooked game;
//    OrthographicCamera camera;
//    private FreeTypeFontGenerator fontGenerator;
//    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
//    private BitmapFont font;
//    private Stage stage;


    public LogRegScreen(Undercooked game) {
        super();
        this.game = game;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
//        super.camera.update();
//        Gdx.gl.glClearColor(54/255f, 84/255f, 120/255f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        camera.update();
//        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
//        stage.draw();
//        camera.update();
//        game.batch.setProjectionMatrix(camera.combined);
//
//        game.batch.begin();
//        font.draw(game.batch, "Undercooked!", 200, 300);
//        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
//        game.font.draw(game.batch, "Tap anywhere to get to main screen!", 100, 100);
//        game.batch.end();

//        if (Gdx.input.isTouched()) {
//            game.setScreen(new MainMenuScreen(game));
//            dispose();
//        }

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        super.show();
        Table table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        super.stage.addActor(table);

//
//        labelStyle = new Label.LabelStyle();
//        labelStyle.font = super.font;
//        label = new Label("Undercooked!", labelStyle) ;
//        label.setPosition(200, 300);
//        super.stage.addActor(label);

        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));
        TextButton register = new TextButton("Register", skin);
        TextButton login = new TextButton("Login", skin);
        register.setPosition(230, 180);
        login.setPosition(230, 130);
        super.stage.addActor(register);
        super.stage.addActor(login);

        table.add(register);
        table.row();
        table.add(login);


        register.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RegisterScreen(game));
            }
        });

        login.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
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
//        game.batch.dispose();
        font.dispose();
        fontGenerator.dispose();
        stage.dispose();
    }
}
