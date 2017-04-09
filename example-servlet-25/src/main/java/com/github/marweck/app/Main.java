package com.github.marweck.app;

import com.github.marweck.tomcat.launcher.TomcatLauncher;

/**
 * Main class, creates the tomcat launcher.
 *
 * @author Marcio Carvalho
 *
 */
public class Main {

	private Main() {
	}

	public static void main(String[] args) throws Exception {
		new TomcatLauncher(8080).launch();
	}
}