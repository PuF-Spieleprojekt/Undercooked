package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

public class ProfileScreen extends ControlScreen implements Screen {
    //TODO add Collection that shows which skins are already usable
    final Networking net;
    TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("playermodel/player_naked_sprites.txt"));
    TextureAtlas textureAtlas2 = new TextureAtlas(Gdx.files.internal("playermodel/player_naked_sprites_apron.txt"));


    public ProfileScreen(Networking net){
        super();
        this.net = net;
    }

    @Override
    public void show() {
        super.show();


        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);
        stage.addActor(table);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        final Slider slider = new Slider(1,3,1,false, skin);
        Label username = new Label("Username: ", skin);
        //TODO: GetUsername from Server
        Label usernamePart2 = new Label("Testplayer1", skin);
        Label chooseSkin = new Label("Choose Skin: ", skin);
        final Image image = new Image(new SpriteDrawable(textureAtlas.createSprite("down1")));




        table.add(username).left();
        table.add(usernamePart2).left();
        table.row();
        table.add(image).pad(50,10,50,0).right();
        table.row();
        table.add(chooseSkin).right();
        table.add(slider).left();

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(slider.getValue() == 1){
                    System.out.println("Value1");
                    image.setDrawable(new SpriteDrawable(textureAtlas.createSprite("down1")));
                }else if(slider.getValue() == 2){
                    System.out.println("Value2");
                    image.setDrawable(new SpriteDrawable(textureAtlas.createSprite("up1")));
                } else if(slider.getValue() == 3){
                    image.setDrawable(new SpriteDrawable(textureAtlas.createSprite("up1")));
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

    @Override
    public void dispose() {

    }
}
