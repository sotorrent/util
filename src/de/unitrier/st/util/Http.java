package de.unitrier.st.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    public static HttpURLConnection openHttpConnection(String url,
                                                       String requestMethod,
                                                       boolean followRedirects,
                                                       int timeout) throws IOException {
        if (!url.startsWith("http")) {
            throw new IllegalArgumentException("Protocol is neither http nor https.");
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
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
}
