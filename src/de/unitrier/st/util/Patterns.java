package de.unitrier.st.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patterns {
    // for the basic regex, see https://stackoverflow.com/a/6041965, alternative: https://stackoverflow.com/a/29288898
    // see also https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    public static final String urlRegex = "(?:https?|ftp)://(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))(?:/[\\w.,@^=%&:/~+-]+)?(?:\\?[\\w.,@?^=%&:/~+#-]+)?(?:#[\\w.,@?^=%&:/~+#-]+)?";
    // the regex string is needed for the Link classes in project so-posthistory-extractor
    public static final Pattern url = Pattern.compile(urlRegex);

    // pattern to extract protocol from URL
    public static final Pattern protocol = Pattern.compile("^(https?|ftp)");

    // pattern to extract domain (including subdomains) from URL
    public static final Pattern completeDomain = Pattern.compile("(?:https?|ftp)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))");

    // pattern to extract root domain from domain string
    public static final Pattern rootDomain = Pattern.compile("([\\w_-]+\\.[\\w_-]+)$");

    // pattern to extract path from URL
    public static final Pattern path = Pattern.compile("(?:https?|ftp)://(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))/([\\w.,@^=%&:/~+-]+)");

    public static String extractProtocol(String url) {
        // protocol
        Matcher protocolMatcher = Patterns.protocol.matcher(url);
        if (protocolMatcher.find()) {
            throw new IllegalArgumentException("Extraction of protocol failed for URL: " + url);
        }
        return protocolMatcher.group(1);
    }

    public static String extractCompleteDomain(String url) {
        Matcher completeDomainMatcher = Patterns.completeDomain.matcher(url);
        if (!completeDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of complete domain failed for URL: " + url);
        }
        return completeDomainMatcher.group(1);
    }

    public static String extractRootDomain(String completeDomain) {
        Matcher rootDomainMatcher = Patterns.rootDomain.matcher(completeDomain);
        if (!rootDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of root domain failed for URL: " + url);
        }
        return rootDomainMatcher.group(1);
    }

    public static String extractPath(String url) {
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

        return path;
    }
}
