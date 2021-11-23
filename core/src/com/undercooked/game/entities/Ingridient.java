package com.undercooked.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.Text;


public class Ingridient {
    String name;
   public Texture texture;
   public Rectangle hitbox;


    public Ingridient(String name, Texture texture, Rectangle hitbox){
        this.name = name;
        this.texture = texture;
        this.hitbox = hitbox;
    }
}
