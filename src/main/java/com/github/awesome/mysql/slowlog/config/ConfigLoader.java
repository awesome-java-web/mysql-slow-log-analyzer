package com.github.awesome.mysql.slowlog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {

    private static final String DEFAULT_CONFIGURATION_FILE_NAME = "mysql-slow-log-analyzer.yml";

    public Config loadFromFile() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_CONFIGURATION_FILE_NAME)) {
            if (inputStream == null) {
                return loadDefault();
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(inputStream, Config.class);
        }
    }

    public Config loadDefault() {
        Config config = new Config();
        config.setAnalysisReportPath("target/");
        config.setSlowQueryThreshold(500);
        config.setTopSlowQueries(10);
        config.setTopLockTimeQueries(10);
        config.setTopRowsSentQueries(10);
        config.setTopRowsExaminedQueries(10);
        config.setTopWorstRowsEfficiencyQueries(10);
        return config;
    }

}
