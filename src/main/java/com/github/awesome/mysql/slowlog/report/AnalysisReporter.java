package com.github.awesome.mysql.slowlog.report;

import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;

import java.io.IOException;

public interface AnalysisReporter {
    void report(AnalysisResult result) throws IOException;
}
