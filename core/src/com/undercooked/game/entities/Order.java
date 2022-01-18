package com.undercooked.game.entities;

import com.badlogic.gdx.utils.Timer;

import java.util.Set;

public class Order {
    private Recipe recipe;
    private int secondsUntilCanceled;
    private float orderTime;

    public Order(Recipe recipe, int secondsUntilCanceled, float orderTime){
        this.recipe = recipe;
        this.secondsUntilCanceled = secondsUntilCanceled;
        this.orderTime = orderTime;
    }

    public int setServedGetPoints(){
        return (secondsUntilCanceled/20) > 0 ? (secondsUntilCanceled/20) : 1 ;
    }

    public String toString() {
        return recipe.toString() + " " + secondsUntilCanceled;
    }

}
