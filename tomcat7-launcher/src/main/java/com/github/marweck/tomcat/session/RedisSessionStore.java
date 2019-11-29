package com.github.marweck.tomcat.session;

import org.apache.catalina.Context;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.tomcat.RedissonSessionManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Adds Redis Session capabilities to Tomcat
 */
public class RedisSessionStore implements SessionStore {

    private final String redisUrl;

    private final String clientName;

    private final String password;

    public RedisSessionStore(String redisUrl, String clientName, String password) {
        this.redisUrl = redisUrl;
        this.clientName = clientName;
        this.password = password;
    }

    public RedisSessionStore(String redisUrl) {
        this(redisUrl, null, null);
    }

    @Override
    public void configureSessionStore(Context ctx) {

        Config config = new Config();

        SingleServerConfig serverConfig = config.useSingleServer().setAddress(redisUrl);
        config.setCodec(new FstCodec());

        if (clientName != null) {
            serverConfig.setClientName(clientName);
        }

        if (password != null) {
            serverConfig.setPassword(password);
        }

        try {
            File configFile = File.createTempFile("tomcat-redisson", ".json");
            configFile.deleteOnExit();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(configFile))) {
                bw.write(config.toJSON());
                RedissonSessionManager redisManager = new RedissonSessionManager();
                redisManager.setConfigPath(configFile.getAbsolutePath());
                ctx.setManager(redisManager);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Redis configuration", e);
        }
    }
}
