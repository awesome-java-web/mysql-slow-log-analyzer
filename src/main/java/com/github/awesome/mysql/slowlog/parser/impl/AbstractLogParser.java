package com.github.awesome.mysql.slowlog.parser.impl;

import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.enums.LogLineIdentifier;
import com.github.awesome.mysql.slowlog.enums.StringSymbols;
import com.github.awesome.mysql.slowlog.parser.LogParser;
import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.parser.model.ParsableLogEntry;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public abstract class AbstractLogParser implements LogParser {

    protected final Config config;

    @Override
    public List<AnalyzableLogEntry> parse(Stream<String> stream) {
        List<AnalyzableLogEntry> analyzableLogEntries = new ArrayList<>();
        List<ParsableLogEntry> parsableLogEntries = transform(stream);
        for (ParsableLogEntry parsableLogEntry : parsableLogEntries) {
            BigDecimal queryTimeMillis = parseQueryTimeMillis(parsableLogEntry);
            if (queryTimeMillis.compareTo(BigDecimal.valueOf(config.getSlowQueryThreshold())) < 0) {
                continue;
            }
            AnalyzableLogEntry analyzableLogEntry = new AnalyzableLogEntry();
            analyzableLogEntry.setIdentifier(generateIdentifier(parsableLogEntry));
            analyzableLogEntry.setUser(parseUser(parsableLogEntry));
            analyzableLogEntry.setDatabase(parseDatabase(parsableLogEntry));
            analyzableLogEntry.setHost(parseHost(parsableLogEntry));
            analyzableLogEntry.setQueryTimeMillis(queryTimeMillis);
            analyzableLogEntry.setLockTimeMillis(parseLockTimeMillis(parsableLogEntry));
            analyzableLogEntry.setRowsSent(parseRowsSent(parsableLogEntry));
            analyzableLogEntry.setRowsExamined(parseRowsExamined(parsableLogEntry));
            analyzableLogEntry.setTimestamp(parseTimestamp(parsableLogEntry));
            analyzableLogEntry.setDatetime(formatTimestamp(analyzableLogEntry.getTimestamp()));
            analyzableLogEntry.setSql(parsableLogEntry.getSql());
            analyzableLogEntries.add(analyzableLogEntry);
        }
        return analyzableLogEntries;
    }

    private List<ParsableLogEntry> transform(Stream<String> rawLogStream) {
        List<String> rawLogLines = new ArrayList<>();
        List<ParsableLogEntry> parsableLogEntries = new ArrayList<>();

        List<String> rawLogList = rawLogStream.collect(toList());
        Iterator<String> iterator = rawLogList.iterator();
        while (iterator.hasNext()) {
            final String line = iterator.next();
            if (line.startsWith(LogLineIdentifier.LOG_TIME.getPrefix())) {
                break;
            }
            iterator.remove();
        }

        for (String line : rawLogList) {
            final boolean isNewLogEntryStarted = line.startsWith(LogLineIdentifier.LOG_TIME.getPrefix());
            if (isNewLogEntryStarted && !rawLogLines.isEmpty()) {
                ParsableLogEntry parsableLogEntry = transform(rawLogLines);
                parsableLogEntries.add(parsableLogEntry);
                rawLogLines.clear();
            }
            rawLogLines.add(line);
        }

        // The last raw log entry
        ParsableLogEntry parsableLogEntry = transform(rawLogLines);
        parsableLogEntries.add(parsableLogEntry);

        return parsableLogEntries;
    }

    private ParsableLogEntry transform(List<String> rawLogLines) {
        ParsableLogEntry parsableLogEntry = new ParsableLogEntry();
        parsableLogEntry.setUserAndHost(rawLogLines.get(LogLineIdentifier.USER_AND_HOST.getIndex()));
        parsableLogEntry.setPerformanceCriteria(rawLogLines.get(LogLineIdentifier.PERFORMANCE_CRITERIA.getIndex()));
        parsableLogEntry.setTimestamp(rawLogLines.get(LogLineIdentifier.TIMESTAMP.getIndex()));
        List<String> sqlStatements = rawLogLines.subList(LogLineIdentifier.SQL.getIndex(), rawLogLines.size());
        final String sql = sqlStatements.stream().map(String::trim).collect(joining(StringSymbols.SPACE.getSymbol()));
        parsableLogEntry.setSql(sql);
        return parsableLogEntry;
    }

    private String formatTimestamp(long timestamp) {
        return Instant.ofEpochSecond(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
