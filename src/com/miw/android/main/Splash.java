package com.miw.android.main;

import com.miw.android.notes.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

/**
 * Creaci�n del Splash, pantalla de intro
 * @author Pablo
 *
 */
public class Splash extends Activity {
	private long ms = 0;
	private long splashTime = 2000;
	private boolean splashActive = true;
	private boolean paused = false;

	/**
	 * Crear el Splash
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hides the titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash);

		Thread mythread = new Thread() {
			public void run() {
				try {
					while (splashActive && ms < splashTime) {
						if (!paused)
							ms = ms + 100;
						sleep(100);
					}
				} catch (Exception e) {
				} finally {
					Intent intent = new Intent(Splash.this, Home.class);
					startActivity(intent);
					finish();
				}
			}
		};
		mythread.start();
	}
}