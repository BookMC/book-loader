package org.bookmc.loader.api.vessel.author;

public class Author {
    private final String name;
    private final String github;
    private final String email;

    public Author(String name, String github, String email) {
        this.name = name;
        this.github = github;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getGithub() {
        return github;
    }

    public String getEmail() {
        return email;
    }
}
