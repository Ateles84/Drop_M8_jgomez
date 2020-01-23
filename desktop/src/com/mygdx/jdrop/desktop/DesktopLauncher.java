package com.mygdx.jdrop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.jdrop.DropJoan;
import com.mygdx.jdrop.JDrop;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		//Modifiquem les mesures de la finestra en desktop
		config.width = 800;
		config.height = 480;

		new LwjglApplication(new JDrop(), config);
	}
}
