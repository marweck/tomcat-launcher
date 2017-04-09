package com.github.marweck.app;

import com.github.marweck.tomcat.launcher.TomcatLauncher;

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
		new TomcatLauncher(8081).launch();
	}
}