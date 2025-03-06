package com.github.awesome.mysql.slowlog.reader.impl;

import com.github.awesome.mysql.slowlog.exception.HttpRemoteFileDownloadException;
import com.github.awesome.mysql.slowlog.reader.RemoteFileLogReader;
import com.github.awesome.mysql.slowlog.util.DigestHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class HttpFileLogReader implements RemoteFileLogReader {

    private static final String OS_TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Override
    public Stream<String> readAsStream(String source) throws IOException {
        Path localFilePath = downloadFile(source);
        return Files.lines(localFilePath);
    }

    @Override
    public Path downloadFile(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (
            Response response = client.newCall(request).execute();
            InputStream byteStream = getResponseInputStream(url, response)
        ) {
            final String localFileIdentifier = DigestHelper.md5(url);
            Path outputPath = Paths.get(OS_TEMP_DIR, String.format("mysql_slow_log_%s.txt", localFileIdentifier));
            Files.copy(byteStream, outputPath);
            return outputPath;
        } catch (Exception e) {
            throw new HttpRemoteFileDownloadException("Failed to download file from " + url, e);
        }
    }

    @NotNull
    private InputStream getResponseInputStream(final String url, Response response) {
        if (response.isSuccessful()) {
            return Objects.requireNonNull(response.body()).byteStream();
        }

        final String message = String.format(
            "Failed to download file from %s. Response code = %d, message = %s",
            url, response.code(), response.message()
        );
        throw new HttpRemoteFileDownloadException(message);
    }

}
