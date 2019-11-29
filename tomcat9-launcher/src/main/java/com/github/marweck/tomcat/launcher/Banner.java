package com.github.marweck.tomcat.launcher;

import java.io.PrintStream;
import java.util.Properties;

/**
 * Holds the tomcat banner string.
 * <p>
 * Created using http://patorjk.com/software/taag
 *
 * @author Marcio Carvalho
 */
public class Banner {

    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_RED = "\u001B[31m";

    private static final String ANSI_BLUE = "\u001B[34m";

    private static final String TOMCAT_VERSION = tomcatVersion();

    /**
     * Friendly Tomcat banner
     */
    private static final String[] BANNER_LINES = {
            ANSI_BLUE + "                                                             ",
            "\t████████╗ ██████╗ ███╗   ███╗ ██████╗ █████╗ ████████╗   ██╗ ██╗       ",
            "\t╚══██╔══╝██╔═══██╗████╗ ████║██╔════╝██╔══██╗╚══██╔══╝   ╚██╗╚██╗      ",
            "\t   ██║   ██║   ██║██╔████╔██║██║     ███████║   ██║       ╚██╗╚██╗     ",
            "\t   ██║   ██║   ██║██║╚██╔╝██║██║     ██╔══██║   ██║       ██╔╝██╔╝     ",
            "\t   ██║   ╚██████╔╝██║ ╚═╝ ██║╚██████╗██║  ██║   ██║      ██╔╝██╔╝      ",
            "\t   ╚═╝    ╚═════╝ ╚═╝     ╚═╝ ╚═════╝╚═╝  ╚═╝   ╚═╝      ╚═╝ ╚═╝       ",
            "\t                                           " + TOMCAT_VERSION,
            ANSI_RESET + "                                                            "};

    private static String tomcatVersion() {
        try {
            Properties properties = new Properties();
            properties.load(Banner.class.getClassLoader().getResourceAsStream("version.properties"));

            return "v " + properties.getProperty("tomcat.version");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Private constructor
     */
    private Banner() {
    }

    /**
     * Prints a friendly Tomcat banner on the print stream
     *
     * @param out
     */
    public static void printBanner(PrintStream out) {
        for (String line : Banner.BANNER_LINES) {
            out.println(line);
        }
    }

    /**
     * Turns this text ANSI RED
     *
     * @param s
     * @return
     */
    public static String redText(String s) {
        return Banner.ANSI_RED + s + Banner.ANSI_RESET;
    }

    /**
     * Turns this text ANSI BLUE
     *
     * @param s
     * @return
     */
    public static String blueText(String s) {
        return Banner.ANSI_BLUE + s + Banner.ANSI_RESET;
    }
}
