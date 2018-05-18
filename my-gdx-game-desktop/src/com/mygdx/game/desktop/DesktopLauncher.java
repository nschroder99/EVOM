package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.Physics6;




public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Evom";
        cfg.width = 768 + 32*Physics6.hudWidth;
        cfg.height = 768;
        cfg.resizable = false;
        cfg.forceExit = true;
        new LwjglApplication(new MyGdxGame(), cfg);
	}
}
