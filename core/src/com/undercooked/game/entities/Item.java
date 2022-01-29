package com.undercooked.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.undercooked.game.components.Holdable;

import java.awt.Point;

public class Item {
    protected String name;

    // components
    protected Vector2 position;
    protected Texture texture;
    private Holdable holdable;

    public Item(String name) {
        this.name = name;
    }

    public Item(String name, Vector2 position, Texture texture, boolean isHoldable) {
        this.name = name;
        this.position = position;
        this.texture = texture;
        if (isHoldable) holdable = new Holdable();
    }

    public boolean isHoldable() {
        return (holdable == null);
    }
}
