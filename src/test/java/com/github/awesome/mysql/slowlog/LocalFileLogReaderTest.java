package com.github.awesome.mysql.slowlog;

import com.github.awesome.mysql.slowlog.analyzer.QueryAnalyzer;
import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;
import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.config.ConfigLoader;
import com.github.awesome.mysql.slowlog.parser.LogParser;
import com.github.awesome.mysql.slowlog.parser.impl.DefaultLogParser;
import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.reader.LogReader;
import com.github.awesome.mysql.slowlog.reader.impl.LocalFileLogReader;
import com.github.awesome.mysql.slowlog.report.AnalysisReporter;
import com.github.awesome.mysql.slowlog.report.impl.HtmlAnalysisReporter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalFileLogReaderTest {

    @Test
    void testLocalFileLogReader() throws IOException {
        ConfigLoader configLoader = new ConfigLoader();
        Config config = configLoader.loadFromFile();
        LogReader localFileLogReader = new LocalFileLogReader();
        Stream<String> logStream = localFileLogReader.readAsStream(config.getSlowLogPath());
        LogParser defaultLogParser = new DefaultLogParser(config);
        List<AnalyzableLogEntry> analyzableLogEntries = defaultLogParser.parse(logStream);
        QueryAnalyzer queryAnalyzer = new QueryAnalyzer(config);
        AnalysisResult analysisResult = queryAnalyzer.analyze(analyzableLogEntries);
        AnalysisReporter htmlAnalysisReporter = new HtmlAnalysisReporter(config);
        htmlAnalysisReporter.report(analysisResult);
        assertEquals(3, analyzableLogEntries.size());
        assertEquals("909.278", analysisResult.getSlowestQuery().getQueryTimeMillis().toPlainString());
        assertEquals("0.122", analysisResult.getLongestLockTimeQuery().getLockTimeMillis().toPlainString());
        assertEquals(2144, analysisResult.getMaxRowsSentQuery().getRowsSent());
        assertEquals(2144, analysisResult.getMaxRowsExaminedQuery().getRowsExamined());
        assertEquals(
            BigDecimal.valueOf(1).divide(BigDecimal.valueOf(2084), AnalyzableLogEntry.DEFAULT_BIG_DECIMAL_SCALE, RoundingMode.HALF_UP),
            analysisResult.getWorstRowsEfficiencyQuery().getRowsEfficiency()
        );
    }

}
