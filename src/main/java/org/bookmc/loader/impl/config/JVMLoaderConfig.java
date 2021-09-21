package org.bookmc.loader.impl.config;

import org.bookmc.loader.api.config.LoaderConfig;

public class JVMLoaderConfig implements LoaderConfig {
    @Override
    public boolean getOption(String option, boolean def) {
        return Boolean.parseBoolean(System.getProperty(option));
    }

    @Override
    public String getOption(String option, String def) {
        return System.getProperty(option, def);
    }

    @Override
    public void setOption(String option, boolean value) {
        System.setProperty(option, Boolean.toString(value));
    }

    @Override
    public void setOption(String option, String value) {
        System.setProperty(option, value);
    }

    @Override
    public boolean hasOption(String option) {
        return System.getProperties().containsKey(option);
    }
}
