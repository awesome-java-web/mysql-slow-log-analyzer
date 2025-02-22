package com.github.awesome.mysql.slowlog.reader;

import java.io.IOException;
import java.util.stream.Stream;

public interface LogReader {
    Stream<String> readAsStream(String source) throws IOException;
}
