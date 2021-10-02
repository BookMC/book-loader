package org.bookmc.loader.api.config;

public interface LoaderConfig {
    boolean getOption(String option, boolean def);

    String getOption(String option, String def);

    void setOption(String option, boolean value);

    void setOption(String option, String value);

    boolean hasOption(String option);
}
