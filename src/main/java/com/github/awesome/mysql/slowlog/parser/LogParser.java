package com.github.awesome.mysql.slowlog.parser;

import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import com.github.awesome.mysql.slowlog.parser.model.ParsableLogEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public interface LogParser {
    List<AnalyzableLogEntry> parse(Stream<String> stream);

    String generateIdentifier(ParsableLogEntry segment);

    String parseUser(ParsableLogEntry segment);

    String parseHost(ParsableLogEntry segment);

    BigDecimal parseQueryTimeMillis(ParsableLogEntry segment);

    BigDecimal parseLockTimeMillis(ParsableLogEntry segment);

    long parseRowsSent(ParsableLogEntry segment);

    long parseRowsExamined(ParsableLogEntry segment);

    long parseTimestamp(ParsableLogEntry segment);
}
