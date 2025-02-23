package com.github.awesome.mysql.slowlog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StringSymbols {

    AT("@"),

    EMPTY(""),

    SPACE(" "),

    SEMICOLON(";"),

    SQUARE_BRACKET_LEFT("["),

    SQUARE_BRACKET_RIGHT("]"),

    ;

    private final String symbol;

}
