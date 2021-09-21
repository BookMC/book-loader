package org.bookmc.loader.api.service;

import java.nio.file.Path;
import java.util.Map;

/**
 * The game data-service provides the loader with data provided to launch
 * (Minecraft specifically however should work with any game). Feel free
 * to open an issue if there is any problems with the game you're trying to
 * use this with.
 */
public interface GameDataService {
    /**
     * @return The game of the name that has been launched
     */
    String getGameName();

    /**
     * @return The version of the game that is being launched
     */
    String getGameVersion();

    /**
     * @return The entrypoint of the game we are trying to load
     * with transformation modifications. To state the given
     * method MUST have a method called "main" and has the modifiers
     * public and static, takes a single parameter (String[]) and a return type of void.
     * If the following requirements are not met then the program will fail due to failing
     * to find a usable entrypoint.
     */
    String getGameEntrypoint();

    /**
     * The path the game is currently "working in". If your game provides the working directory
     * as an argument then you can use the "arguments" parameter to take your data.
     *
     * @param arguments The arguments passed when the game is launched
     * @return The path to the current directory the game is supposed to work in
     */
    Path getWorkingDirectory(Map<String, String> arguments);
}
