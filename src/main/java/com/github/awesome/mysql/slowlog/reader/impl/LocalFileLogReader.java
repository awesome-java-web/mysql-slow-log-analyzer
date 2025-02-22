package com.github.awesome.mysql.slowlog.reader.impl;

import com.github.awesome.mysql.slowlog.reader.LogReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LocalFileLogReader implements LogReader {

    @Override
    public Stream<String> readAsStream(String source) throws IOException {
        Path filePath = Paths.get(source);
        return Files.lines(filePath);
    }

}
