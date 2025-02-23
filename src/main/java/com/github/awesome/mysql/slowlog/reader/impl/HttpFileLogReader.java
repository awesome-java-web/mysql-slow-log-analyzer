package com.github.awesome.mysql.slowlog.reader.impl;

import com.github.awesome.mysql.slowlog.exception.HttpRemoteFileDownloadException;
import com.github.awesome.mysql.slowlog.reader.RemoteFileLogReader;
import com.github.awesome.mysql.slowlog.util.DigestHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class HttpFileLogReader implements RemoteFileLogReader {

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
            final String localFileIdentifier = DigestHelper.md5Base64Hash(url);
            Path outputPath = Paths.get(String.format("mysql_slow_log_%s.txt", localFileIdentifier));
            Files.copy(byteStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            return outputPath;
        } catch (Exception e) {
            throw new HttpRemoteFileDownloadException("Failed to download file from " + url, e);
        }
    }

    @NotNull
    private InputStream getResponseInputStream(final String url, Response response) {
        if (!response.isSuccessful()) {
            final String message = String.format(
                "Failed to download file from %s. Response code = %d, message = %s",
                url, response.code(), response.message()
            );
            throw new HttpRemoteFileDownloadException(message);
        }

        ResponseBody body = response.body();
        if (body == null) {
            throw new HttpRemoteFileDownloadException("Response body is null");
        }

        return body.byteStream();
    }

}
