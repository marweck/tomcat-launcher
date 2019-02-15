package com.github.marweck.app;

import com.github.marweck.tomcat.launcher.Tomcat9Launcher;

/**
 * Main class. Creates the tomcat launcher.
 *
 * @author Marcio Carvalho
 *
 */
public class Main {

	private Main() {
	}

	public static void main(String[] args) throws Exception {
		new Tomcat9Launcher(8081).launch();
	}
}