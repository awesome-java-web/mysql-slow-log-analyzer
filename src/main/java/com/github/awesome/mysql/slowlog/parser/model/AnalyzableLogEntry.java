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

    private String identifier;

    private String host;

    private String user;

    private String database;

    private BigDecimal queryTimeMillis;

    private BigDecimal lockTimeMillis;

    private long rowsSent;

    private long rowsExamined;

    private long timestamp;

    private String datetime;

    private String sql;

}
