package com.undercooked.game.entities;

import com.badlogic.gdx.utils.Timer;

import java.util.Set;

public class Order {
    private Timer timer;
    private int secondsUntilCanceled;
    private double tip;

    public Order(Timer timer, Set<Ingridient> ingridients, double tip){
        this.timer = timer;
        this.secondsUntilCanceled = secondsUntilCanceled;
        this.tip = tip;
    }

    private void reduceTip(){
        tip = tip*(secondsUntilCanceled/3);
    }

}
