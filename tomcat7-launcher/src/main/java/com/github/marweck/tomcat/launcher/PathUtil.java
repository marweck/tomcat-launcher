package com.github.marweck.tomcat.launcher;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.catalina.startup.Constants;
import org.apache.tomcat.util.buf.UriUtil;

/**
 * Utilities related to resources paths
 *
 * @author Marcio Carvalho
 */
public class PathUtil {

    /**
     * Most common used document root locations
     */
    private static final String[] COMMON_DOC_ROOTS = {"src/main/webapp", "public", "static", "webapp"};

    /**
     * Private constructor
     */
    private PathUtil() {
    }

    /**
     * Temporary directory creation. Used for tomcat work directories
     * configuration.
     *
     * @param prefix
     * @param suffix
     * @return
     */
    public static File createTempDir(String prefix, String suffix) {
        try {
            File dirFile = File.createTempFile(prefix + ".", suffix);

            if (!dirFile.delete()) {
                // no-op
            }

            dirFile.mkdir();
            dirFile.deleteOnExit();

            return dirFile;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Find the path of the most likely document root locations
     *
     * @return
     */
    public static File getDocumentBase() {

        File file = getWarFileDocumentRoot();

        // If not a war archive maybe it is an exploded war
        file = file != null ? file : getExplodedWarFileDocumentRoot();

        // Or maybe there is a document root in a well-known location
        file = file != null ? file : getCommonDocumentRoot();

        if (file == null) {
            throw new IllegalArgumentException("Document base location not found");
        }

        return file;
    }

    /**
     * Looks for the document root location inside a war file
     *
     * @return
     */
    private static File getExplodedWarFileDocumentRoot() {

        File file = getCodeSourceArchive();

        if (file != null && file.exists() && file.getAbsolutePath().contains("/WEB-INF/")) {
            String path = file.getAbsolutePath();
            path = path.substring(0, path.indexOf("/WEB-INF/"));
            return new File(path);
        }

        return null;
    }

    private static File getWarFileDocumentRoot() {
        return getArchiveFileDocumentRoot(".war");
    }

    /**
     * Looks for the document root inside the given extension
     *
     * @param extension
     * @return
     */
    private static File getArchiveFileDocumentRoot(String extension) {

        File file = getCodeSourceArchive();

        if (file != null && file.exists() && !file.isDirectory() && file.getName().toLowerCase().endsWith(extension)) {
            return file.getAbsoluteFile();
        }

        return null;
    }

    /**
     * Checks if any of the common document root locations exist
     *
     * @return
     */
    private static File getCommonDocumentRoot() {

        for (String commonDocRoot : COMMON_DOC_ROOTS) {
            File root = new File(commonDocRoot);

            if (root.exists() && root.isDirectory()) {
                return root.getAbsoluteFile();
            }
        }

        return null;
    }

    /**
     * Get the runtime location of the code, be it inside a jar or war
     *
     * @return
     */
    private static File getCodeSourceArchive() {

        try {
            CodeSource codeSource = PathUtil.class.getProtectionDomain().getCodeSource();
            URL location = codeSource == null ? null : codeSource.getLocation();

            if (location == null) {
                return null;
            }

            String path = location.getPath();
            URLConnection connection = location.openConnection();

            if (connection instanceof JarURLConnection) {
                path = ((JarURLConnection) connection).getJarFile().getName();
            }

            if (path.indexOf("!/") != -1) {
                path = path.substring(0, path.indexOf("!/"));
            }

            return new File(path);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Looks for the context.xml Tomcat application configuration file.
     *
     * @param documentBase
     * @return
     */
    public static URL getWebappConfigFile(File documentBase) {

        if (documentBase.isDirectory()) {
            return getWebappConfigFileFromDirectory(documentBase);
        } else {
            return getWebappConfigFileFromJar(documentBase);
        }
    }

    /**
     * Looks for the config "META-INF/context.xml" file from a directory
     *
     * @param docBase
     * @return
     */
    private static URL getWebappConfigFileFromDirectory(File docBase) {

        URL result = null;
        File webAppContextXml = new File(docBase, Constants.ApplicationContextXml);

        if (webAppContextXml.exists()) {
            try {
                result = webAppContextXml.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Unable to determine web application context.xml " + docBase, e);
            }
        }

        return result;
    }

    /**
     * Looks for the context.xml configuration file inside a jar
     *
     * @param docBase
     * @return
     */
    private static URL getWebappConfigFileFromJar(File docBase) {

        URL result = null;
        JarFile jar = null;

        try {
            jar = new JarFile(docBase);
            JarEntry entry = jar.getJarEntry(Constants.ApplicationContextXml);

            if (entry != null) {
                result = UriUtil.buildJarUrl(docBase, Constants.ApplicationContextXml);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to determine web application context.xml " + docBase, e);
        } finally {
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return result;
    }
}