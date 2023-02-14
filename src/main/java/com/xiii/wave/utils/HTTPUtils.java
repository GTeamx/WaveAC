package com.xiii.wave.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public final class HTTPUtils {

    public synchronized static String readUrl(String URL) {

        final HttpGet httpGet = new HttpGet(URL);

        try(final CloseableHttpClient CHC = HttpClientBuilder.create().build()) {

            final CloseableHttpResponse closeableHttpResponse = CHC.execute(httpGet);
            final HttpEntity httpEntity = closeableHttpResponse.getEntity();

            return EntityUtils.toString(httpEntity);

        } catch(final ClientProtocolException e) {
            e.printStackTrace();
            return "CPE_E";
        } catch(final IOException e) {
            e.printStackTrace();
            return "UNKNOWN";
        }

    }

    public synchronized static boolean validate(final String string) {

        final String str = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return string.matches(str);

    }

    public synchronized static void downloadFile(final String URL, final File file) {

        try {

            final java.net.URL uRL = new URL(URL);
            final ReadableByteChannel readableByteChannel = Channels.newChannel(uRL.openStream());
            final FileOutputStream fileOutputStream = new FileOutputStream(file);

            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0L, Long.MAX_VALUE);

        } catch(final Exception exception) {
            exception.printStackTrace();
        }

    }

}
