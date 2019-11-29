package com.github.marweck.tomcat.session;

import org.apache.catalina.Context;

public interface SessionStore {

    /**
     * Configures default session manager - NOOP
     *
     * @param ctx
     *         Tomcat context
     */
    default void configureSessionStore(Context ctx) {
        //no-op, tomcat default session store
    }
}
