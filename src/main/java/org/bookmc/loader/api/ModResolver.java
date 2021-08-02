package org.bookmc.loader.api;

import com.google.gson.JsonParser;

import java.io.File;

public interface ModResolver {
    JsonParser parser = new JsonParser();

    /**
     * This non-default provides the discoverer with an array of files (if wanted).
     * It should after either resolve those files by registering them as candidates
     * or provide it's own files to resolve.
     *
     * @param files An array of files provided to iterate and resolve over.
     */
    void resolve(File[] files);
}
