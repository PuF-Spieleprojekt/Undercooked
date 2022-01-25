package com.undercooked.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.undercooked.game.Networking;
import com.undercooked.game.utilities.enums.Direction;

import java.util.EnumMap;


public class NetworkPlayer {

    private Networking net;
    private Rectangle hitbox;
    private int step = 1;
    private Sprite sprite;
    private String textureString = "down1";
    private Byte direction = 3; // clockwise: 1=up 2=right 3=down 4=left
    private Animation<TextureRegion> cutAnimation;
    private String userID;
    private String userName;
    private boolean hasPlate;


    // some new ways of dealing with directions and where to draw the items being held
    Direction facing = Direction.DOWN;
    public Vector2 holdingPosition;
    private Vector2 holdingOffset = new Vector2(0,0);
    private EnumMap<Direction, Vector2> holdingOffsetDistances = new EnumMap<Direction, Vector2>(Direction.class);


    TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("playermodel/player_apron_yellow.txt"));


    public NetworkPlayer(Networking net){
        this.net = net;
        this.sprite = textureAtlas.createSprite("down1");
        this.hitbox = sprite.getBoundingRectangle();
        this.holdingPosition = new Vector2(hitbox.x, hitbox.y);
        this.userID = net.getUserdata().get("userID");
        this.userName = net.getUserdata().get("userName");

        // initialize direction distances
        holdingOffsetDistances.put(Direction.UP, new Vector2(0, 80));
        holdingOffsetDistances.put(Direction.RIGHT, new Vector2(30, 15));
        holdingOffsetDistances.put(Direction.DOWN, new Vector2(0, -30));
        holdingOffsetDistances.put(Direction.LEFT, new Vector2(-30, 15));
    }

    public void changeTexture(Direction direction){
        if(step == 12){ step = 1;}
        step+=1;
        switch (direction){
            case UP :
                if (step>=5 &&step<=8){
                   changeAnimationStep("up1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeAnimationStep("up2");
                    break;
                } else if (step>=9 &&step<=12){
                    changeAnimationStep("up3");
                    break;
            }

            case DOWN :
                this.cutAnimation = new Animation<TextureRegion>(1f/10f, textureAtlas.findRegions("cut-front"));
                if (step>=5 &&step<=8){
                    changeAnimationStep("down1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeAnimationStep("down2");
                    break;
                }else if(step>=9 &&step<=12){
                    changeAnimationStep("down3");
                    break;
                }

            case LEFT :
                this.cutAnimation = new Animation<TextureRegion>(1f/10f, textureAtlas.findRegions("cut-left"));
                if (step>=5 &&step<=8){
                    changeAnimationStep("left1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeAnimationStep("left2");
                    break;
                }else if(step>=9 &&step<=12){
                    changeAnimationStep("left3");
                    break;
                }

            case RIGHT :
                this.cutAnimation = new Animation<TextureRegion>(1f/10f, textureAtlas.findRegions("cut-right"));
                if (step>=5 &&step<=8){
                    changeAnimationStep("right1");
                    break;
                } else if(step>=1 &&step<=4){
                    changeAnimationStep("right2");
                    break;
                }else if(step>=9 &&step<=12){
                    changeAnimationStep("right3");
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

    private void changeAnimationStep(String name){
        textureString = name;
        sprite.setRegion(textureAtlas.findRegion(name));

    }

    public void changeDirection(Direction d) {
        facing = d;
        changeTexture(d);
        updateHoldingPosition(d);
    }

    private void updateHoldingPosition(Direction d) {
        holdingOffset = holdingOffsetDistances.get(d);
        holdingPosition.x = hitbox.getX() + holdingOffset.x;
        holdingPosition.y = hitbox.getY() + holdingOffset.y;
    }

    public void setNetworkPosition(String x, String y){
        hitbox.setX(Float.parseFloat(x));
        hitbox.setY(Float.parseFloat(y));
    }

    public void setNetworkDirection(String dir){
        Direction d = Direction.valueOf(dir.toUpperCase());
        facing = d;
        changeTexture(d);
        updateHoldingPosition(d);
    }

    public void setHasPlate(boolean carrying){
        hasPlate = carrying;
    }




    public void setUserID(String newID){
        userID = newID;
    }

    public void setUserName(String newUsername){
        userName = newUsername;
    }


    // Getters
    public String getTextureName(){
        return textureString;
    }

    public String getPositionStringX(){
        return String.valueOf(hitbox.x);
    }

    public String getPositionStringY(){
        return String.valueOf(hitbox.y);
    }

    public Sprite getTexture(){
        return sprite;
    }

    public Rectangle getHitbox() { return this.hitbox; }

    public Animation<TextureRegion> getCutAnimation(){ return this.cutAnimation; }

    public String getDirection() { return facing.toString(); }
    public String getUserID () { return userID; }

    public String getUserName () { return userName; }

    public String getPlate() {
        return String.valueOf(hasPlate);
    }
}



