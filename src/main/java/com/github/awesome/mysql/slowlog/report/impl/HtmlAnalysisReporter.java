package com.github.awesome.mysql.slowlog.report.impl;

import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;
import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.enums.StringSymbols;
import com.github.awesome.mysql.slowlog.report.AnalysisReporter;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.stream.Collectors.joining;

@AllArgsConstructor
public class HtmlAnalysisReporter implements AnalysisReporter {

    private static final String HTML_TEMPLATE_NAME_ZH_CN = "mysql-slow-log-analysis-report-template-zh_CN.html";

    private static final String HTML_TEMPLATE_NAME_EN_US = "mysql-slow-log-analysis-report-template-en_US.html";

    private final Config config;

    @Override
    public void report(AnalysisResult result) {
        renderHtml(result, HTML_TEMPLATE_NAME_ZH_CN);
        renderHtml(result, HTML_TEMPLATE_NAME_EN_US);
    }

    private void renderHtml(AnalysisResult result, String reportTemplateName) {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(reportTemplateName)) {
            if (inputStream == null) {
                return;
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            final String template = reader.lines().collect(joining(StringSymbols.SPACE.getSymbol()));
            final String html = template
                .replace("{{totalSlowQueries}}", String.valueOf(result.getTotalSlowQueries()))
                .replace("{{averageQueryTimeMillis}}", result.getAverageQueryTimeMillis().toPlainString())
                .replace("{{slowestQueryTimeMillis}}", result.getSlowestQuery().getQueryTimeMillis().toPlainString())
                .replace("{{averageLockTimeMillis}}", result.getAverageLockTimeMillis().toPlainString())
                .replace("{{longestLockTimeMillis}}", result.getLongestLockTimeQuery().getLockTimeMillis().toPlainString())
                .replace("{{maxRowsSent}}", String.valueOf(result.getMaxRowsSentQuery().getRowsSent()))
                .replace("{{maxRowsExamined}}", String.valueOf(result.getMaxRowsExaminedQuery().getRowsExamined()))
                .replace("{{minRowsEfficiency}}", result.getMinRowsEfficiencyQuery()
                    .getRowsEfficiency()
                    .movePointRight(2)
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString()
                );
            final String reportName = reportTemplateName.replace("-template", StringSymbols.EMPTY.getSymbol());
            Files.write(Paths.get(config.getAnalysisReportPath(), reportName), html.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
