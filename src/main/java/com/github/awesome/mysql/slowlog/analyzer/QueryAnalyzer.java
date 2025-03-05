package com.github.awesome.mysql.slowlog.analyzer;

import com.github.awesome.mysql.slowlog.analyzer.model.AnalysisResult;
import com.github.awesome.mysql.slowlog.config.Config;
import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@AllArgsConstructor
public class QueryAnalyzer {

    private final Config config;

    public AnalysisResult analyze(List<AnalyzableLogEntry> entries) {
        AnalysisResult result = new AnalysisResult();
        result.setTotalSlowQueries(entries.size());
        result.setSlowestQuery(findSlowestQuery(entries));
        result.setLongestLockTimeQuery(findLongestLockTimeQuery(entries));
        result.setMaxRowsSentQuery(findMaxRowsSentQuery(entries));
        result.setMaxRowsExaminedQuery(findMaxRowsExaminedQuery(entries));
        result.setTopSlowQueries(findTopSlowQueries(entries));
        result.setTotalHitCountMap(computeTotalHitCountMap(entries));
        return result;
    }

    private AnalyzableLogEntry findSlowestQuery(List<AnalyzableLogEntry> entries) {
        BigDecimal longestQueryTime = BigDecimal.ZERO;
        AnalyzableLogEntry slowestQuery = null;
        for (AnalyzableLogEntry entry : entries) {
            if (entry.getQueryTimeMillis().compareTo(longestQueryTime) > 0) {
                longestQueryTime = entry.getQueryTimeMillis();
                slowestQuery = entry;
            }
        }
        return slowestQuery;
    }

    private AnalyzableLogEntry findLongestLockTimeQuery(List<AnalyzableLogEntry> entries) {
        BigDecimal longestLockTime = BigDecimal.ZERO;
        AnalyzableLogEntry longestLockTimeQuery = null;
        for (AnalyzableLogEntry entry : entries) {
            if (entry.getLockTimeMillis().compareTo(longestLockTime) > 0) {
                longestLockTime = entry.getLockTimeMillis();
                longestLockTimeQuery = entry;
            }
        }
        return longestLockTimeQuery;
    }

    private AnalyzableLogEntry findMaxRowsSentQuery(List<AnalyzableLogEntry> entries) {
        long maxRowsSent = 0;
        AnalyzableLogEntry maxRowsSentQuery = null;
        for (AnalyzableLogEntry entry : entries) {
            if (entry.getRowsSent() > maxRowsSent) {
                maxRowsSent = entry.getRowsSent();
                maxRowsSentQuery = entry;
            }
        }
        return maxRowsSentQuery;
    }

    private AnalyzableLogEntry findMaxRowsExaminedQuery(List<AnalyzableLogEntry> entries) {
        long maxRowsExamined = 0;
        AnalyzableLogEntry maxRowsExaminedQuery = null;
        for (AnalyzableLogEntry entry : entries) {
            if (entry.getRowsExamined() > maxRowsExamined) {
                maxRowsExamined = entry.getRowsExamined();
                maxRowsExaminedQuery = entry;
            }
        }
        return maxRowsExaminedQuery;
    }

    private List<AnalyzableLogEntry> findTopSlowQueries(List<AnalyzableLogEntry> entries) {
        return entries.stream()
            .sorted((e1, e2) -> e2.getQueryTimeMillis().compareTo(e1.getQueryTimeMillis()))
            .limit(config.getTopSlowQueries())
            .collect(toList());
    }

    private Map<String, Long> computeTotalHitCountMap(List<AnalyzableLogEntry> entries) {
        return entries.stream()
            .map(AnalyzableLogEntry::getIdentifier)
            .collect(groupingBy(
                Function.identity(),
                counting()
            ));
    }

}
