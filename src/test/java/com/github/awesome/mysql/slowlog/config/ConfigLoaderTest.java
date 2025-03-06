package com.github.awesome.mysql.slowlog.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigLoaderTest {

    @Test
    void testLoadDefault() {
        ConfigLoader configLoader = new ConfigLoader();
        Config config = configLoader.loadDefault();

        assertEquals("target/", config.getAnalysisReportPath());
        assertEquals(500, config.getSlowQueryThreshold());
        assertEquals(10, config.getTopSlowQueries());
    }

}
