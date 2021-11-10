package com.undercooked.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.undercooked.game.Undercooked;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Drop";
		config.width = 1600;
		config.height = 960;
		config.useHDPI = true;
		new LwjglApplication(new Undercooked(), config);
	}
}
