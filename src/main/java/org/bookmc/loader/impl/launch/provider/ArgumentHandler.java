package org.bookmc.loader.impl.launch.provider;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ArgumentHandler {
    private final List<String> args;

    public ArgumentHandler(String[] args) {
        this.args = Arrays.asList(args);
    }

    public Optional<String> get(String key) {
        int index = args.indexOf("--" + key);
        return Optional.ofNullable(index == -1 ? null : args.get(index + 1));
    }
}
