package com.github.awesome.mysql.slowlog.parser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class ParsableLogEntry implements Serializable {

    private String userAndHost;

    private String performanceCriteria;

    private String timestamp;

    private String sql;

}
