package org.sotorrent.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patterns {
    // for the basic regex, see https://stackoverflow.com/a/6041965, alternative: https://stackoverflow.com/a/29288898
    // see also https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    public static final String urlRegex = "(?:https?|ftp)://(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))(?:/[\\w.,@^=%&:/~+-]+)?(?:\\([\\w.,%:+-]+\\))?(?:\\?[\\w.,@?^=%&:/~+-]+)?(?:#[\\w.,@?^=%&:/~+#-]+(?:\\([\\w.,%:+-]+\\))?)?";
    // the regex string is needed for the Link classes in project so-posthistory-extractor
    public static final Pattern url = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);

    // pattern to extract protocol from URL
    public static final Pattern protocol = Pattern.compile("^(https?|ftp)", Pattern.CASE_INSENSITIVE);

    // pattern to extract domain (including subdomains) from URL
    public static final Pattern completeDomain = Pattern.compile("^(?:https?|ftp)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))", Pattern.CASE_INSENSITIVE);

    // pattern to extract root domain from domain string
    public static final Pattern rootDomain = Pattern.compile("([\\w_-]+\\.[\\w_-]+)$", Pattern.CASE_INSENSITIVE);

    // pattern to extract path (including fragment identifier) from URL
    public static final Pattern path = Pattern.compile("^(?:https?|ftp)://(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))/([\\w.,@^=%&:/~+-]+)(\\?[^?#]+)?(#[^#]+)?", Pattern.CASE_INSENSITIVE);

    // (valid or malformed) IPv4
    public static final Pattern ipv4 = Pattern.compile("https?://[.\\d]+");

    public static String extractProtocolFromUrl(String url) {
        // protocol
        Matcher protocolMatcher = Patterns.protocol.matcher(url);
        if (!protocolMatcher.find()) {
            throw new IllegalArgumentException("Extraction of protocol failed for URL: " + url);
        }
        return protocolMatcher.group(1);
    }

    public static String cleanUrl(String url) {
        if (url == null) {
            return null;
        }

        if (url.endsWith(".") || url.endsWith(",") || url.endsWith(":")) {
            url = url.substring(0, url.length()-1);
        }

        if (url.endsWith("&#xA")) {
            url = url.substring(0, url.length()-4);
        }

        return url;
    }

    public static String extractCompleteDomainFromUrl(String url) {
        Matcher completeDomainMatcher = Patterns.completeDomain.matcher(url);
        if (!completeDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of complete domain failed for URL: " + url);
        }
        return completeDomainMatcher.group(1);
    }

    public static String extractRootDomainFromCompleteDomain(String completeDomain) {
        Matcher rootDomainMatcher = Patterns.rootDomain.matcher(completeDomain);
        if (!rootDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of root domain failed for URL: " + url);
        }
        return rootDomainMatcher.group(1);
    }

    public static String extractPathFromUrl(String url) {
        Matcher pathMatcher = Patterns.path.matcher(url);
        if (!pathMatcher.find()) {
            // don't change this, needed to set database column to null (so-posthistory-extractor)
            return null;
        }

        String path = pathMatcher.group(1);
        // remove trailing slash
        if (path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }
        // return null if path only contains whitespaces
        if (path.trim().length() == 0) {
            return null;
        }
        // return null if path only contains punctuation (extracted from Markdown)
        if (path.equals(".") || path.equals(",") || path.equals(":")) {
            return null;
        }

        return path;
    }

    public static String extractQueryFromUrl(String url) {
        Matcher pathMatcher = Patterns.path.matcher(url);
        if (!pathMatcher.find()) {
            // don't change this, needed to set database column to null (so-posthistory-extractor)
            return null;
        }

        return pathMatcher.group(2);
    }

    public static String extractFragmentIdentifierFromUrl(String url) {
        Matcher pathMatcher = Patterns.path.matcher(url);
        if (!pathMatcher.find()) {
            // don't change this, needed to set database column to null (so-posthistory-extractor)
            return null;
        }

        return pathMatcher.group(3);
    }

    public static boolean isIpAddress(String url) {
        Matcher ipv4Matcher = Patterns.ipv4.matcher(url);
        return ipv4Matcher.find();
    }
}
