package com.github.awesome.mysql.slowlog.reader;

import com.github.awesome.mysql.slowlog.analyzer.QueryAnalyzer;
import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;
import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.config.ConfigLoader;
import com.github.awesome.mysql.slowlog.enums.StringSymbols;
import com.github.awesome.mysql.slowlog.parser.LogParser;
import com.github.awesome.mysql.slowlog.parser.impl.DefaultLogParser;
import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.reader.impl.HttpFileLogReader;
import com.github.awesome.mysql.slowlog.report.AnalysisReporter;
import com.github.awesome.mysql.slowlog.report.impl.HtmlAnalysisReporter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoteFileLogReaderTest {

    @Test
    void testHttpFileLogReader() throws IOException {
        ConfigLoader configLoader = new ConfigLoader();
        Config config = configLoader.loadFromFile();

        MockWebServer server = new MockWebServer();
        Path logFilePath = Paths.get(config.getSlowLogPath());
        final String url = server.url(logFilePath.toString()).toString();
        Stream<String> lines = Files.lines(logFilePath);
        MockResponse response = new MockResponse();
        response.setBody(lines.collect(joining(StringSymbols.NEWLINE.getSymbol())));
        response.setResponseCode(200);
        server.enqueue(response);

        RemoteFileLogReader httpFileLogReader = new HttpFileLogReader();
        Stream<String> logStream = httpFileLogReader.readAsStream(url);

        LogParser defaultLogParser = new DefaultLogParser(config);
        List<AnalyzableLogEntry> analyzableLogEntries = defaultLogParser.parse(logStream);

        QueryAnalyzer queryAnalyzer = new QueryAnalyzer(config);
        AnalysisResult analysisResult = queryAnalyzer.analyze(analyzableLogEntries);

        AnalysisReporter htmlAnalysisReporter = new HtmlAnalysisReporter(config);
        htmlAnalysisReporter.report(analysisResult);

        lines.close();
        server.shutdown();

        assertEquals(4277, analyzableLogEntries.size());
        assertEquals("33185.268", analysisResult.getSlowestQuery().getQueryTimeMillis().toPlainString());
        assertEquals("2.632", analysisResult.getLongestLockTimeQuery().getLockTimeMillis().toPlainString());
        assertEquals(12438, analysisResult.getMaxRowsSentQuery().getRowsSent());
        assertEquals(981484, analysisResult.getMaxRowsExaminedQuery().getRowsExamined());
    }

}
