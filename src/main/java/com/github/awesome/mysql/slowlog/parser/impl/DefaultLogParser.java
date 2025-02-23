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
    public String generateIdentifier(ParsableLogEntry entry) {
        return DigestHelper.md5Base64Hash(entry.getSql());
    }

    @Override
    public String parseUser(ParsableLogEntry entry) {
        final String userAndDatabase = parseUserAndHost(entry)[0];
        final int leftBracketIndex = userAndDatabase.indexOf(StringSymbols.SQUARE_BRACKET_LEFT.getSymbol());
        return userAndDatabase.substring(0, leftBracketIndex);
    }

    @Override
    public String parseDatabase(ParsableLogEntry entry) {
        final String userAndDatabase = parseUserAndHost(entry)[0];
        final int leftBracketIndex = userAndDatabase.indexOf(StringSymbols.SQUARE_BRACKET_LEFT.getSymbol());
        return userAndDatabase.substring(leftBracketIndex + 1, userAndDatabase.length() - 1);
    }

    @Override
    public String parseHost(ParsableLogEntry entry) {
        return parseUserAndHost(entry)[1];
    }

    @Override
    public BigDecimal parseQueryTimeMillis(ParsableLogEntry entry) {
        return new BigDecimal(parsePerformanceCriteria(entry)[0]).movePointRight(3);
    }

    @Override
    public BigDecimal parseLockTimeMillis(ParsableLogEntry entry) {
        return new BigDecimal(parsePerformanceCriteria(entry)[1]).movePointRight(3);
    }

    @Override
    public long parseRowsSent(ParsableLogEntry entry) {
        return Long.parseLong(parsePerformanceCriteria(entry)[2]);
    }

    @Override
    public long parseRowsExamined(ParsableLogEntry entry) {
        return Long.parseLong(parsePerformanceCriteria(entry)[3]);
    }

    @Override
    public long parseTimestamp(ParsableLogEntry entry) {
        final String timestamp = entry.getTimestamp()
            .replace(LogLineIdentifier.TIMESTAMP.getPrefix(), StringSymbols.EMPTY.getSymbol())
            .trim()
            .replace(StringSymbols.SEMICOLON.getSymbol(), StringSymbols.EMPTY.getSymbol());
        return Long.parseLong(timestamp);
    }

    private String[] parseUserAndHost(ParsableLogEntry entry) {
        final String temp = entry.getUserAndHost()
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

    private String[] parsePerformanceCriteria(ParsableLogEntry entry) {
        String[] tokens = entry.getPerformanceCriteria()
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
