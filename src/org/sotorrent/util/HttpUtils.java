package org.sotorrent.util;

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

    public static boolean success(HttpURLConnection conn)  {
        boolean success;
        try {
            // see https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#2xx_Success
            success = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 226);
        } catch (IOException e) {
            success = false;
        }
        return success;
    }

    public static boolean redirect(HttpURLConnection conn)  {
        boolean redirect;
        try {
            // see https://en.wikipedia.org/wiki/List_of_HTTP_status_codes#3xx_Redirection
            redirect = (conn.getResponseCode() >= 300 && conn.getResponseCode() <= 308);
        } catch (IOException e) {
            redirect = false;
        }
        return redirect;
    }

    public static boolean tooManyRequests(HttpURLConnection conn) {
        boolean tooManyRequests;
        try {
            tooManyRequests = (conn.getResponseCode() == 429);  // may also be 403
        } catch (IOException e) {
            tooManyRequests = false;
        }
        return tooManyRequests;
    }
}
