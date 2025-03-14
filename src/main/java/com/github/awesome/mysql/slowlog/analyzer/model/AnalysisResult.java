package com.github.awesome.mysql.slowlog.analyzer.model;

import com.github.awesome.mysql.slowlog.parser.model.AnalyzableLogEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class AnalysisResult implements Serializable {

    private int totalSlowQueries;

    private AnalyzableLogEntry slowestQuery;

    private AnalyzableLogEntry longestLockTimeQuery;

    private AnalyzableLogEntry maxRowsSentQuery;

    private AnalyzableLogEntry maxRowsExaminedQuery;

    private List<AnalyzableLogEntry> topSlowQueries;

    private Map<String, Long> totalHitCountMap;

    private List<QueryTimeDistributionBarChartItem> queryTimeDistributionBarChartData;
}
