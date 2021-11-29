package com.undercooked.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    String name;
    public Rectangle hitbox;
    private int step = 1;
    private Texture texture;
    private Texture lookup1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/up1.png"));
    private Texture lookup2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/up2.png"));
    private Texture lookup3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/up3.png"));
    private Texture lookdown1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/down1.png"));
    private Texture lookdown2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/down2.png"));
    private Texture lookdown3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/down3.png"));
    private Texture lookleft1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/left1.png"));
    private Texture lookleft2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/left2.png"));
    private Texture lookleft3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/left3.png"));
    private Texture lookright1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/right1.png"));
    private Texture lookright2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/right2.png"));
    private Texture lookright3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/right3.png"));


    public Player(String name, Rectangle hitbox){
        this.name = name;
        this.hitbox = hitbox;
        this.texture = lookdown1;
    }

    public void changeTexture(String direction){
        if(step == 5){ step = 1;}
        System.out.println(step);
        step+=1;
        switch (direction){
            case "up" :
                if (step == 4){
                    this.texture = lookup1;
                    break;
                } else if( step == 1 ){
                    this.texture = lookup2;
                    break;
                }

            case "down" :
                if (step == 4){
                    this.texture = lookdown1;
                    break;
                } else if( step == 1 ){
                    this.texture = lookdown2;
                    break;
                }

            case "left" :
                if (step == 4){
                    this.texture = lookleft1;
                    break;
                } else if( step == 1 ){
                    this.texture = lookleft2;
                    break;
                }

            case "right" :
                if (step == 4){
                    this.texture = lookright1;
                    break;
                } else if( step == 1 ){
                    this.texture = lookright2;
                    break;
                }
        }
    }
    public Texture getTexture(){
        return this.texture;
    }
}


