package com.github.awesome.mysql.slowlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogLineIdentifier {

    LOG_TIME("# Time:", 0),

    USER_AND_HOST("# User@Host:", 1),

    PERFORMANCE_CRITERIA("# Query_time:", 2),

    TIMESTAMP("SET timestamp=", 3),

    SQL("SELECT", 4),

    ;

    private final String prefix;

    private final int index;

}
