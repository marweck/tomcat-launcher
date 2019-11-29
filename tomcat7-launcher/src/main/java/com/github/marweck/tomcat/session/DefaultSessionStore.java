package com.github.marweck.tomcat.session;

import org.apache.catalina.Context;

public class DefaultSessionStore implements SessionStore {

    @Override
    public void configureSessionStore(Context ctx) {
        //No-op. Uses default tomcat session implementation
    }
}
