package org.bookmc.loader.api.vessel.author;

/**
 * Metadata used by the {@link org.bookmc.loader.api.vessel.ModVessel} to provide who
 * authored the mod. Mods can use this data to display it to the user as cool gimmics
 * or maybe you want to contact the developer yourself? Intersting...
 */
public class Author {
    private final String name;
    private final String github;
    private final String email;

    public Author(String name, String github, String email) {
        this.name = name;
        this.github = github;
        this.email = email;
    }

    /**
     * @return The name of the author
     */
    public String getName() {
        return name;
    }

    /**
     * @return The GitHub of the author
     */
    public String getGithub() {
        return github;
    }

    /**
     * @return The email of the author
     */
    public String getEmail() {
        return email;
    }
}
