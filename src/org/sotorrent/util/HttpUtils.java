package org.sotorrent.util;

import org.sotorrent.util.exceptions.RateLimitExceededException;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static HttpURLConnection openHttpConnection(String url,
                                                       String requestMethod,
                                                       boolean followRedirects,
                                                       int timeout) throws IOException {
        HttpURLConnection conn;

        if (url.startsWith("http://")) {
            conn = (HttpURLConnection) new URL(url).openConnection();
        } else if (url.startsWith("https://")) {
            conn = (HttpsURLConnection) new URL(url).openConnection();
        } else {
            throw new IllegalArgumentException("Protocol is not http.");
        }

        // some API return a "403 Forbidden" if no user agent is specified
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.13; rv:10.0) Gecko/20100101 Firefox/10.0");
        conn.setInstanceFollowRedirects(followRedirects);
        conn.setConnectTimeout(timeout);
        conn.setRequestMethod(requestMethod);  // see Javadoc of this method for possible values
        conn.connect();

        return conn;
    }

    public static boolean isSuccess(HttpURLConnection conn) throws IOException {
        // see https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#2xx_Success
        return (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 226);
    }

    public static boolean isRedirect(HttpURLConnection conn) throws IOException {
        // see https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#3xx_Redirection
        return (conn.getResponseCode() >= 300 && conn.getResponseCode() <= 308);
    }

    public static void checkTooManyRequests(HttpURLConnection conn) throws IOException, RateLimitExceededException {
        if (conn.getResponseCode() == 429) {  // may also be 403
            throw new RateLimitExceededException(conn.getResponseCode() + ": " + conn.getResponseMessage());
        }
    }
}
