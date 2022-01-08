package com.undercooked.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Undercooked extends Game {

	SpriteBatch batch;
	BitmapFont font;
	ShapeRenderer shape;
	Networking net;

	float timeCount = 0;
	int timer;

	public void create() {
		batch = new SpriteBatch();
		// Use LibGDX's default Arial font.
		font = new BitmapFont();
		shape = new ShapeRenderer();
		net = new Networking();
		this.setScreen(new LogRegScreen(this, net));
		timer = 30;
	}

	public void render() {
		super.render(); // important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
	}

}
