package com.github.awesome.mysql.slowlog;

import com.github.awesome.mysql.slowlog.analyzer.QueryAnalyzer;
import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;
import com.github.awesome.mysql.slowlog.parser.LogParser;
import com.github.awesome.mysql.slowlog.parser.impl.DefaultLogParser;
import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.reader.LogReader;
import com.github.awesome.mysql.slowlog.reader.impl.LocalFileLogReader;
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
        final String logFilePath = "src/test/resources/mysql_slow_log.txt";
        LogReader localFileLogReader = new LocalFileLogReader();
        Stream<String> logStream = localFileLogReader.readAsStream(logFilePath);
        LogParser defaultLogParser = new DefaultLogParser();
        List<AnalyzableLogEntry> analyzableLogEntries = defaultLogParser.parse(logStream);
        QueryAnalyzer queryAnalyzer = new QueryAnalyzer();
        AnalysisResult analysisResult = queryAnalyzer.analyze(analyzableLogEntries);
        assertEquals(3, analyzableLogEntries.size());
        assertEquals("0.909278", analysisResult.getSlowestQuery().getQueryTime().toPlainString());
        assertEquals("0.000122", analysisResult.getLongestLockTimeQuery().getLockTime().toPlainString());
        assertEquals(2144, analysisResult.getMaxRowsSentQuery().getRowsSent());
        assertEquals(2144, analysisResult.getMaxRowsExaminedQuery().getRowsExamined());
        assertEquals(
            BigDecimal.valueOf(1).divide(BigDecimal.valueOf(2084), AnalyzableLogEntry.DEFAULT_BIG_DECIMAL_SCALE, RoundingMode.HALF_UP),
            analysisResult.getMinRowsEfficiencyQuery().getRowsEfficiency()
        );
    }

}
