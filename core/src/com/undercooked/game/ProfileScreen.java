package com.undercooked.game;

import com.badlogic.gdx.Game;
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

import java.util.concurrent.ExecutionException;

import javax.swing.text.Utilities;

public class ProfileScreen extends ControlScreen implements Screen {
    //TODO add Collection that shows which skins are already usable
    final Networking net;
    final Undercooked game;

    TextureAtlas atlasYellow = new TextureAtlas(Gdx.files.internal("playermodel/player_apron_yellow.txt"));
    TextureAtlas atlasGreen = new TextureAtlas(Gdx.files.internal("playermodel/player_apron_green.txt"));
    TextureAtlas atlasBlue = new TextureAtlas(Gdx.files.internal("playermodel/player_apron_blue.txt"));
    TextureAtlas atlasRed = new TextureAtlas(Gdx.files.internal("playermodel/player_apron_red.txt"));


    public ProfileScreen(Undercooked game, Networking net) throws ExecutionException, InterruptedException {
        super();
        this.game = game;
        this.net = net;

        GlobalUtilities.itmeList = net.retrieveStorageData("items", "item");
        GlobalUtilities.skinAsString = GlobalUtilities.itmeList.get(0);
    }

    @Override
    public void show() {
        super.show();
        final String playername = net.getUsername();

        Skin skin = new Skin(Gdx.files.internal("star-soldier-ui.json"));

        Table table = new Table();
        table.setFillParent(true);
        table.setY(-80);
        stage.addActor(table);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        final Slider slider = new Slider(1,4,1,false, skin);
        Label username = new Label("Username: ", skin);

        Label usernamePart2 = new Label(playername, skin);
        Label chooseSkin = new Label("Choose Skin: ", skin);
        final Image image = new Image(new SpriteDrawable(atlasYellow.createSprite("down1")));




        table.add(username).left();
        table.add(usernamePart2).left();
        table.row();
        table.add(image).pad(50,10,50,0).right();
        table.row();
        table.add(chooseSkin).right();
        table.add(slider).left();

        super.backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GlobalUtilities.itmeList.set(0,GlobalUtilities.skinAsString);

                try {
                    net.updateItemCollectionData("items", "item", "Skins", GlobalUtilities.itmeList);
                    game.setScreen(new MainMenuScreen(game, net));

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //TODO: Add more Skins.
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(slider.getValue() == 1){
                    System.out.println("Value1");
                    image.setDrawable(new SpriteDrawable(atlasYellow.createSprite("down1")));
                    GlobalUtilities.skinAsString = "playermodel/player_apron_yellow.txt";
                }else if(slider.getValue() == 2){
                    System.out.println("Value2");
                    image.setDrawable(new SpriteDrawable(atlasGreen.createSprite("down1")));
                    GlobalUtilities.skinAsString = "playermodel/player_apron_green.txt";
                } else if(slider.getValue() == 3){
                    image.setDrawable(new SpriteDrawable(atlasBlue.createSprite("down1")));
                    GlobalUtilities.skinAsString = "playermodel/player_apron_blue.txt";
                }else if(slider.getValue() == 4){
                    image.setDrawable(new SpriteDrawable(atlasRed.createSprite("down1")));
                    GlobalUtilities.skinAsString = "playermodel/player_apron_red.txt";
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
    super.dispose();
    atlasYellow.dispose();
    atlasBlue.dispose();
    atlasGreen.dispose();
    atlasRed.dispose();

    }
}
