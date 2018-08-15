package org.sotorrent.util;

import com.google.common.base.CharMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patterns {
    // for the basic regex, see https://stackoverflow.com/a/6041965, alternative: https://stackoverflow.com/a/29288898
    // see also https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    private static final String protocolRegex = "https?|ftp";
    private static final String domainRegex = "[\\w_\\-]+(?:(?:\\.[\\w_\\-]+)+)";
    private static final String rootDomainRegex = "([\\w_\\-]+\\.[\\w_\\-]+)$";
    private static final String allowedCharacters = "\\w.,@^=%&:/~+\\-";
    private static final String bracketExpression = "\\([" + allowedCharacters + "]+\\)";
    private static final String pathRegex = "(?:[" + allowedCharacters + "]+)?(?:" + bracketExpression + ")?";
    private static final String queryRegex = "\\?[" + allowedCharacters + "\\?]*";
    private static final String fragmentIdentifierRegex = "#[" + allowedCharacters + "?#!]+(?:" + bracketExpression + ")?";
    public static final String urlRegex; // the regex string is needed for the Link classes in project so-posthistory-extractor
    public static final Pattern url, protocol, completeDomain, rootDomain, path;

    // pattern to detect (valid or malformed) IPv4
    public static final Pattern ipv4 = Pattern.compile("https?://[.\\d]+");

    static {
        urlRegex = encloseInNonCapturingGroup(protocolRegex) + "://" + domainRegex + makeOptional(encloseInNonCapturingGroup(pathRegex)) + makeOptional(encloseInNonCapturingGroup(queryRegex)) + makeOptional(encloseInNonCapturingGroup(fragmentIdentifierRegex));
        url = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);

        // pattern to extract protocol from URL
        protocol = Pattern.compile("^" + encloseInCapturingGroup(protocolRegex), Pattern.CASE_INSENSITIVE);

        // pattern to extract domain (including subdomains) from URL
        completeDomain = Pattern.compile("^" + encloseInNonCapturingGroup(protocolRegex) + "://" + encloseInCapturingGroup(domainRegex), Pattern.CASE_INSENSITIVE);

        // pattern to extract root domain from domain string
        rootDomain = Pattern.compile(rootDomainRegex, Pattern.CASE_INSENSITIVE);

        // pattern to extract path (including query and fragment identifier) from URL
        path = Pattern.compile(encloseInNonCapturingGroup(protocolRegex) + "://" + domainRegex + makeOptional(encloseInCapturingGroup(pathRegex)) + makeOptional(encloseInCapturingGroup(queryRegex)) + makeOptional(encloseInCapturingGroup(fragmentIdentifierRegex)), Pattern.CASE_INSENSITIVE);
    }

    private static String makeOptional(String regex) {
        return regex + "?";
    }

    private static String encloseInNonCapturingGroup(String regex) {
        return "(?:" + regex + ")";
    }

    private static String encloseInCapturingGroup(String regex) {
        return "(" + regex + ")";
    }

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

        while (url.endsWith(".") || url.endsWith(",") || url.endsWith(":") || url.endsWith(";")) {
            url = url.substring(0, url.length()-1);
        }

        while (url.endsWith("&#xA") || url.endsWith("&#xD")) {
            url = url.substring(0, url.length()-4);
        }

        return url;
    }

    /**
     * Heuristic to test if a match is inside a Markdown inline code.
     * (uneven number of backtick characters before and after match)
     * @param matcher the matcher to test
     * @param content the content in which the match was found
     * @return true if match is located inside Markdown inline code
     */
    public static boolean inInlineCode(Matcher matcher, String content) {
        int backticksBefore =  CharMatcher.is('`').countIn(content.substring(0, matcher.start()));
        int backticksAfter =  CharMatcher.is('`').countIn(content.substring(matcher.end()));
        return backticksBefore > 0 && backticksAfter > 0 && backticksBefore%2 != 0 && backticksAfter%2 != 0;
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
        // remove leading slash
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
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
