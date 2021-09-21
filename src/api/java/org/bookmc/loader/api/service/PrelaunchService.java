package org.bookmc.loader.api.service;

import org.bookmc.loader.api.environment.GameEnvironment;

import java.util.Map;

public interface PrelaunchService {
    void prelaunch(Map<String, String> arguments, GameEnvironment environment);
}
