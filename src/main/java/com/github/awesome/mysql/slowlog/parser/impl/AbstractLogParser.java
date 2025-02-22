package com.github.awesome.mysql.slowlog.parser.impl;

import com.github.awesome.mysql.slowlog.enums.LogLineIdentifier;
import com.github.awesome.mysql.slowlog.parser.LogParser;
import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.parser.model.ParsableLogEntry;
import com.github.awesome.mysql.slowlog.util.StringSymbols;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public abstract class AbstractLogParser implements LogParser {

    @Override
    public List<AnalyzableLogEntry> parse(Stream<String> stream) {
        List<AnalyzableLogEntry> analyzableLogEntries = new ArrayList<>();
        List<ParsableLogEntry> parsableLogEntries = transform(stream);
        for (ParsableLogEntry parsableLogEntry : parsableLogEntries) {
            AnalyzableLogEntry analyzableLogEntry = new AnalyzableLogEntry();
            analyzableLogEntry.setIdentifier(generateIdentifier(parsableLogEntry));
            analyzableLogEntry.setUser(parseUser(parsableLogEntry));
            analyzableLogEntry.setHost(parseHost(parsableLogEntry));
            analyzableLogEntry.setQueryTime(parseQueryTime(parsableLogEntry));
            analyzableLogEntry.setLockTime(parseLockTime(parsableLogEntry));
            analyzableLogEntry.setRowsSent(parseRowsSent(parsableLogEntry));
            analyzableLogEntry.setRowsExamined(parseRowsExamined(parsableLogEntry));
            analyzableLogEntry.setRowsEfficiency(calculateRowsEfficiency(analyzableLogEntry));
            analyzableLogEntry.setTimestamp(parseTimestamp(parsableLogEntry));
            analyzableLogEntry.setSql(parsableLogEntry.getSql());
            analyzableLogEntries.add(analyzableLogEntry);
        }
        return analyzableLogEntries;
    }

    private List<ParsableLogEntry> transform(Stream<String> rawLogStream) {
        List<String> rawLogLines = new ArrayList<>();
        List<ParsableLogEntry> parsableLogEntries = new ArrayList<>();

        rawLogStream.forEach(line -> {
            final boolean isNewLogEntryStarted = line.startsWith(LogLineIdentifier.LOG_TIME.getPrefix());
            if (isNewLogEntryStarted && !rawLogLines.isEmpty()) {
                ParsableLogEntry parsableLogEntry = transform(rawLogLines);
                parsableLogEntries.add(parsableLogEntry);
                rawLogLines.clear();
            }
            rawLogLines.add(line);
        });

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
        final String sql = sqlStatements.stream().map(String::trim).collect(joining(StringSymbols.SPACE));
        parsableLogEntry.setSql(sql);
        return parsableLogEntry;
    }

    private BigDecimal calculateRowsEfficiency(AnalyzableLogEntry analyzableLogEntry) {
        BigDecimal rowsSent = BigDecimal.valueOf(analyzableLogEntry.getRowsSent());
        BigDecimal rowsExamined = BigDecimal.valueOf(analyzableLogEntry.getRowsExamined());
        return rowsSent.divide(rowsExamined, AnalyzableLogEntry.DEFAULT_BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

}
