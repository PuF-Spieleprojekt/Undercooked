package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FillViewport;

public abstract class ControlScreen implements Screen{

//    final Undercooked game;
    OrthographicCamera camera;
    protected FreeTypeFontGenerator fontGenerator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    protected BitmapFont font;
    protected Stage stage;
    protected Label label;
    protected Label.LabelStyle labelStyle;

    public ControlScreen(){

    }


    @Override
    public void show(){
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        FillViewport viewport = new FillViewport( 800, 480, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("AgentOrange.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 35;
        fontParameter.borderWidth = 2;
        fontParameter.borderColor = Color.FIREBRICK;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

         labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
         label = new Label("Undercooked!", labelStyle) ;
        label.setPosition(200, 300);
        stage.addActor(label);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(54 / 255f, 84 / 255f, 120 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
//        camera.update();
    }

    @Override
    public void dispose(){
        stage.dispose();
        font.dispose();
        fontGenerator.dispose();

    }
}