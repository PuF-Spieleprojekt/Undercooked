package com.undercooked.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectSet;

public class Player {
    String name;
    private Rectangle hitbox;
    private int step = 1;
    private Sprite sprite;
    private String textureString = "down1";

    TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("playermodel/player_naked_sprites.txt"));

    public Player(String name){
        this.name = name;
        this.sprite = textureAtlas.createSprite("down1");
        this.hitbox = sprite.getBoundingRectangle();
    }

    public void changeTexture(String direction){
        if(step == 12){ step = 1;}
        step+=1;
        switch (direction){
            case "up" :
                if (step>=5 &&step<=8){
                   changeDirection("up1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeDirection("up2");
                    break;
                } else if (step>=9 &&step<=12){
                    changeDirection("up3");
                    break;
            }

            case "down" :
                if (step>=5 &&step<=8){
                    changeDirection("down1");;
                    break;
                } else if(step>=1 &&step<=4){
                    changeDirection("down2");
                    break;
                }else if(step>=9 &&step<=12){
                    changeDirection("down3");
                    break;
                }

            case "left" :
                if (step>=5 &&step<=8){
                    changeDirection("left1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeDirection("left2");
                    break;
                }else if(step>=9 &&step<=12){
                    changeDirection("left3");
                    break;
                }

            case "right" :
                if (step>=5 &&step<=8){
                    changeDirection("right1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeDirection("right2");
                    break;
                }else if(step>=9 &&step<=12){
                    changeDirection("right3");
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

    private void changeDirection(String name){
        textureString = name;
        sprite.setRegion(textureAtlas.findRegion(name));

    }

    public String getTextureName(){
        return textureString;
    }

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


    public Sprite getTexture(){
        return sprite;
    }
    public Rectangle getHitbox() { return this.hitbox; }
}


