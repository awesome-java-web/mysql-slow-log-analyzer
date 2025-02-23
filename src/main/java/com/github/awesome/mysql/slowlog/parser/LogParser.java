package com.github.awesome.mysql.slowlog.parser;

import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.parser.model.ParsableLogEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public interface LogParser {
    List<AnalyzableLogEntry> parse(Stream<String> stream);

    String generateIdentifier(ParsableLogEntry entry);

    String parseUser(ParsableLogEntry entry);

    String parseDatabase(ParsableLogEntry entry);

    String parseHost(ParsableLogEntry entry);

    BigDecimal parseQueryTimeMillis(ParsableLogEntry entry);

    BigDecimal parseLockTimeMillis(ParsableLogEntry entry);

    long parseRowsSent(ParsableLogEntry entry);

    long parseRowsExamined(ParsableLogEntry entry);

    long parseTimestamp(ParsableLogEntry entry);
}
