package com.undercooked.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    String name;
    public Rectangle hitbox;
    private int step = 1;
    private Texture texture;
    private final Texture lookup1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/up1.png"));
    private final Texture lookup2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/up2.png"));
    private final Texture lookup3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/up3.png"));
    private final Texture lookdown1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/down1.png"));
    private final Texture lookdown2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/down2.png"));
    private final Texture lookdown3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/down3.png"));
    private final Texture lookleft1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/left1.png"));
    private final Texture lookleft2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/left2.png"));
    private final Texture lookleft3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/left3.png"));
    private final Texture lookright1 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/right1.png"));
    private final Texture lookright2 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/right2.png"));
    private final Texture lookright3 = new Texture(Gdx.files.internal("playermodel/PNG/Sequence/right3.png"));


    public Player(String name, Rectangle hitbox){
        this.name = name;
        this.hitbox = hitbox;
        this.texture = lookdown1;
    }

    public void changeTexture(String direction){
        if(step == 12){ step = 1;}
        System.out.println(step);
        step+=1;
        switch (direction){
            case "up" :
                if (step>=5 &&step<=8){
                    this.texture = lookup1;
                    break;
                } else if(step>=1 &&step<=4){
                    this.texture = lookup2;
                    break;
                } else if (step>=9 &&step<=12){
                    this.texture = lookup3;
                    break;
            }

            case "down" :
                if (step>=5 &&step<=8){
                    this.texture = lookdown1;
                    break;
                } else if(step>=1 &&step<=4){
                    this.texture = lookdown2;
                    break;
                }else if(step>=9 &&step<=12){
                    this.texture = lookdown3;
                    break;
                }

            case "left" :
                if (step>=5 &&step<=8){
                    this.texture = lookleft1;
                    break;
                } else if(step>=1 &&step<=4){
                    this.texture = lookleft2;
                    break;
                }else if(step>=9 &&step<=12){
                    this.texture = lookleft3;
                    break;
                }

            case "right" :
                if (step>=5 &&step<=8){
                    this.texture = lookright1;
                    break;
                } else if(step>=1 &&step<=4){
                    this.texture = lookright2;
                    break;
                }else if(step>=9 &&step<=12){
                    this.texture = lookright3;
                    break;
                }
        }
    }

    public void checkBoundaries(){
        // make sure the bucket stays within the screen bounds
        if (hitbox.x < 0)
            hitbox.x = 0;
        if (hitbox.x > 800 - 64)
            hitbox.x = 800 - 64;
        if (hitbox.y < 0)
            hitbox.y = 0;
        if (hitbox.y > 480 - 64)
            hitbox.y = 480 - 64;
    }

    public void collisionDetection(RectangleMapObject blockingObject){
        // bucket can't cross objects with propertie "blocked
        if (blockingObject.getRectangle().overlaps(hitbox)) {
            if (blockingObject.getRectangle().x > hitbox.x) {
                hitbox.x = hitbox.x - 10;
            } else if (blockingObject.getRectangle().x + blockingObject.getRectangle().width - 5 < hitbox.x) {
                hitbox.x = hitbox.x + 10;
            } else if (blockingObject.getRectangle().y > hitbox.y) {
                hitbox.y = hitbox.y - 10;
            } else if (blockingObject.getRectangle().y + blockingObject.getRectangle().height - 5 < hitbox.y) {
                hitbox.y = hitbox.y + 10;
            }
        }
    }


    public Texture getTexture(){
        return this.texture;
    }
}


