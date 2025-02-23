package com.github.awesome.mysql.slowlog.parser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class AnalyzableLogEntry implements Serializable {

    public static final int DEFAULT_BIG_DECIMAL_SCALE = 6;

    private String identifier;

    private String host;

    private String user;

    private BigDecimal queryTimeMillis;

    private BigDecimal lockTimeMillis;

    private long rowsSent;

    private long rowsExamined;

    private BigDecimal rowsEfficiency;

    private long timestamp;

    private String sql;

}
