package com.github.awesome.mysql.slowlog.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Config implements Serializable {

    @JsonProperty("slow-log-path")
    private String slowLogPath;

    @JsonProperty("analysis-report-path")
    private String analysisReportPath;

    @JsonProperty("slow-query-threshold")
    private int slowQueryThreshold;

    @JsonProperty("top-slow-queries")
    private int topSlowQueries;
}
