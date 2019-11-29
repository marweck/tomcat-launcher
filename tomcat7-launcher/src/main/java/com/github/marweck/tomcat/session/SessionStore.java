package com.github.marweck.tomcat.session;

import org.apache.catalina.Context;

public interface SessionStore {

    /**
     * Configures session manager
     *
     * @param ctx
     *         Tomcat context
     */
    void configureSessionStore(Context ctx);
}
