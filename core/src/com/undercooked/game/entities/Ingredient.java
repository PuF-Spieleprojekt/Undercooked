package com.undercooked.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.sun.org.apache.xpath.internal.operations.Bool;

import org.w3c.dom.Text;


public class Ingredient {
    String name;
   public Texture texture;
   public Rectangle hitbox;
   private Boolean pickedUp = true;
   private Boolean isServed = false;
   private Boolean isPreparing = false;
   //TODO: Create Method for is preaderd additional Textures

    public Ingredient(String name, Texture texture, Rectangle hitbox){
        this.name = name;
        this.texture = texture;
        this.hitbox = hitbox;
    }
    public Texture getTexture(){
        return this.texture;
    }

    public void pickUp(){

        pickedUp = true;
        isPreparing = false;

    }

    public void putDown(RectangleMapObject object){
        if(object.getProperties().containsKey("Serving Area")){
            isServed = true;
        }else if(object.getProperties().containsKey("Preparing Area")){
            isPreparing = true;
        }
        pickedUp = false;
    }

    public Boolean getPickUp(){
        return pickedUp;
    }

    public Boolean getIsServed(){
        return isServed;
    }

    public Boolean getIsPreparing(){
        return isPreparing;
    }
}
