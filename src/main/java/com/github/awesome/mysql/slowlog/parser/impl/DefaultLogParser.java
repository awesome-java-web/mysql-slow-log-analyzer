package com.github.awesome.mysql.slowlog.parser.impl;

import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.enums.LogLineIdentifier;
import com.github.awesome.mysql.slowlog.enums.StringSymbols;
import com.github.awesome.mysql.slowlog.parser.model.ParsableLogEntry;
import com.github.awesome.mysql.slowlog.util.DigestHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DefaultLogParser extends AbstractLogParser {

    public DefaultLogParser(Config config) {
        super(config);
    }

    @Override
    public String generateIdentifier(ParsableLogEntry segment) {
        return DigestHelper.md5Base64Hash(segment.getSql());
    }

    @Override
    public String parseUser(ParsableLogEntry segment) {
        return parseUserAndHost(segment)[0];
    }

    @Override
    public String parseHost(ParsableLogEntry segment) {
        return parseUserAndHost(segment)[1];
    }

    @Override
    public BigDecimal parseQueryTimeMillis(ParsableLogEntry segment) {
        return new BigDecimal(parsePerformanceCriteria(segment)[0]).movePointRight(3);
    }

    @Override
    public BigDecimal parseLockTimeMillis(ParsableLogEntry segment) {
        return new BigDecimal(parsePerformanceCriteria(segment)[1]).movePointRight(3);
    }

    @Override
    public long parseRowsSent(ParsableLogEntry segment) {
        return Long.parseLong(parsePerformanceCriteria(segment)[2]);
    }

    @Override
    public long parseRowsExamined(ParsableLogEntry segment) {
        return Long.parseLong(parsePerformanceCriteria(segment)[3]);
    }

    @Override
    public long parseTimestamp(ParsableLogEntry segment) {
        final String timestamp = segment.getTimestamp()
            .replace(LogLineIdentifier.TIMESTAMP.getPrefix(), StringSymbols.EMPTY.getSymbol())
            .trim()
            .replace(StringSymbols.SEMICOLON.getSymbol(), StringSymbols.EMPTY.getSymbol());
        return Long.parseLong(timestamp);
    }

    private String[] parseUserAndHost(ParsableLogEntry segment) {
        final String temp = segment.getUserAndHost()
            .replace(LogLineIdentifier.USER_AND_HOST.getPrefix(), StringSymbols.EMPTY.getSymbol())
            .trim();
        final int index = temp.lastIndexOf(StringSymbols.SQUARE_BRACKET_RIGHT.getSymbol());
        String[] userAndHost = temp.substring(0, index).split(StringSymbols.AT.getSymbol());
        userAndHost[0] = userAndHost[0].trim();
        userAndHost[1] = userAndHost[1].trim()
            .replace(StringSymbols.SQUARE_BRACKET_LEFT.getSymbol(), StringSymbols.EMPTY.getSymbol())
            .replace(StringSymbols.SQUARE_BRACKET_RIGHT.getSymbol(), StringSymbols.EMPTY.getSymbol());
        return userAndHost;
    }

    private String[] parsePerformanceCriteria(ParsableLogEntry segment) {
        String[] tokens = segment.getPerformanceCriteria()
            .replace(LogLineIdentifier.PERFORMANCE_CRITERIA.getPrefix(), StringSymbols.EMPTY.getSymbol())
            .trim()
            .split(StringSymbols.SPACE.getSymbol());
        List<String> filtered = Arrays.stream(tokens).filter(token -> !token.trim().isEmpty()).collect(toList());
        String[] result = new String[4];
        result[0] = filtered.get(0);
        result[1] = filtered.get(2);
        result[2] = filtered.get(4);
        result[3] = filtered.get(6);
        return result;
    }

}
