package com.undercooked.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends ControlScreen implements Screen {

    final Undercooked game;
    OrthographicCamera camera;
//    private FreeTypeFontGenerator fontGenerator;
//    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
//    private BitmapFont font;
//    private Stage stage;
//    private Label label;
//    private Label.LabelStyle labelStyle;

    public MainMenuScreen(final Undercooked gam) {
        super();
        game = gam;

//        camera = new OrthographicCamera();
//        camera.setToOrtho(false, 800, 480);
//        FillViewport viewport = new FillViewport( 800, 480,camera);
//        stage = new Stage(viewport);
//        Gdx.input.setInputProcessor(stage);
//
//        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("AgentOrange.ttf"));
//        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        fontParameter.size = 35;
//        fontParameter.borderWidth = 2;
//        fontParameter.borderColor = Color.FIREBRICK;
//        fontParameter.color = Color.WHITE;
//        font = fontGenerator.generateFont(fontParameter);
//        fontGenerator.dispose();



//
    }


    @Override
    public void render(float delta) {
        super.render(delta);

//        Gdx.gl.glClearColor(54/255f, 84/255f, 120/255f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
//        stage.draw();

//        ScreenUtils.clear(5, 0.7, 0.9, 1);

//        camera.update();
//        game.batch.setProjectionMatrix(camera.combined);
//
//        game.batch.begin();
//        font.draw(game.batch, "Undercooked!", 200, 300);
////        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
//        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
//        game.batch.end();
//
//        if (Gdx.input.isTouched()) {
//            game.setScreen(new GameScreen(game));
//            dispose();
//        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        super.show();
        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));
        TextButton play = new TextButton("Play", skin);
        TextButton profile = new TextButton("Profile", skin);
        TextButton highscore = new TextButton("Highscore", skin);
        TextButton quit = new TextButton("Quit", skin);

        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        label = new Label("Undercooked!", labelStyle) ;
        label.setPosition(200, 300);        // hardcoded position
        super.stage.addActor(label);
        super.stage.addActor(play);


        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });


        Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);            // hardcoded position
//        table.setDebug(true);
        stage.addActor(table);
        table.add(play);
        table.row();
        table.add(profile);
        table.row();
        table.add(highscore);
        table.row();
        table.add(quit);


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