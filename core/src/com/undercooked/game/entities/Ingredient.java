package com.undercooked.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.sun.org.apache.xpath.internal.operations.Bool;

import org.w3c.dom.Text;


public class Ingredient {
    String name;
   public Texture texture;
    // TODO seperate ingredient from on-screen implementation. we need ingredients for the recipes,
    //  too but the recipes don't use coordinates
   public Rectangle hitbox;
   private Boolean pickedUp = true;
   private Boolean isServed = false;
   private Boolean isPreparing = false;
   private Boolean blockSending = false;

   // String for identifying which player is currently carrying the ingredient
   private String ownerID;
   //TODO: Create Method for is preaderd additional Textures


    public Ingredient(String name, Texture texture, Rectangle hitbox, String ownerID){
        this.name = name;
        this.texture = texture;
        this.hitbox = hitbox;
        this.ownerID = ownerID;
    }

    public Ingredient(String name, Texture texture, Rectangle hitbox){
        this(name, texture, hitbox, "");
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

    public void blockSending (boolean block) { blockSending = block; }

    public Boolean getPickUp(){
        return pickedUp;
    }

    public Boolean getIsServed(){
        return isServed;
    }

    public Boolean getIsPreparing(){
        return isPreparing;
    }

    public Boolean getBlockSending() { return blockSending;  }

 /*   public String getTextureName(){
        return textureString;
    }
*/
    public String getPositionStringX(){
        return String.valueOf(hitbox.x);
    }

    public String getPositionStringY(){
        return String.valueOf(hitbox.y);
    }

    public void setPosition(String x, String y){
        hitbox.x = Float.parseFloat(x);
        hitbox.y = Float.parseFloat(y);
    }

    public void setOwner(String newOwner){
        ownerID = newOwner;
    }

    public String getOwner(){
        return ownerID;
    }

    public String getTextureString(){ return name; }
    @Override
    public String toString() {
        return name;
    }
}
