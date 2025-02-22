package com.github.awesome.mysql.slowlog.reader;

import java.nio.file.Path;

public interface RemoteFileLogReader extends LogReader {
    Path downloadFile(String url);
}
