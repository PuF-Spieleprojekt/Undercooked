package com.undercooked.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class CustomFont {

    protected FreeTypeFontGenerator fontGenerator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    protected BitmapFont font;

    public CustomFont(){
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("AgentOrange.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 35;
        fontParameter.borderWidth = 2;
        fontParameter.borderColor = Color.FIREBRICK;
        fontParameter.color = Color.WHITE;
        this.font = fontGenerator.generateFont(fontParameter);
    }
}
