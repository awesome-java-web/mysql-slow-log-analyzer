package com.github.awesome.mysql.slowlog.analyzer.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class QueryTimeDistributionBarChartItem implements Serializable {

    private String intervalText;

    private int count;
}
