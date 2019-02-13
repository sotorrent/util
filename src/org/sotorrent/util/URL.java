package org.sotorrent.util;

import com.google.common.base.CharMatcher;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL  {
    private static Logger logger = null;
    static {
        // configure logger
        try {
            logger = LogUtils.getClassLogger(URL.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private java.net.URL urlObject;
    private String urlString;
    private String protocol;
    private String completeDomain;
    private String rootDomain;
    private String topLevelDomain;
    private String path;
    private String query;
    private String fragmentIdentifier;

    // for the basic regex, see https://stackoverflow.com/a/6041965, alternative: https://stackoverflow.com/a/29288898
    // see also https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    private static final String protocolRegex = "https?|ftp";
    private static final String completeDomainRegex = "[\\w\\-]+(?:(?:\\.[\\w\\-]+)+)";
    private static final String rootDomainRegex = "([\\w\\-]+\\.([\\w\\-]+))(?:[^\\w\\-.].*)?$";
    private static final String allowedCharacters = "\\w\\-.,@^=%&:/~+";
    private static final String bracketExpression = "\\([" + allowedCharacters + "]+\\)";
    private static final String pathRegex = "/(?:[" + allowedCharacters + "]+)?(?:" + bracketExpression + ")?";
    private static final String queryRegex = "\\?[" + allowedCharacters + "\\?]*";
    private static final String fragmentIdentifierRegex = "#[" + allowedCharacters + "?#!]+(?:" + bracketExpression + ")?";
    private static final Pattern completeDomainPattern;
    private static final Pattern rootDomainPattern;
    public static final String urlRegex; // the regex string is needed for the Link classes in project so-posthistory-extractor
    public static final Pattern urlPattern;

    // regular expressions to match and normalize Stack Overflow links (use redundant escaping to be compatible with SQL)
    private static final Pattern stackOverflowLinkPattern = Pattern.compile("(https?:\\/\\/(?:www.)?stackoverflow\\.com\\/[^\\s).\"]*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern stackOverflowSearchLinkPattern = Pattern.compile("(https?:\\/\\/(?:www.)?stackoverflow\\.com\\/search[^:]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern stackOverflowShortAnswerLinkPattern = Pattern.compile("https?:\\/\\/(?:www.)?stackoverflow\\.com\\/a\\/([\\d]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern stackOverflowLongAnswerLinkPattern = Pattern.compile("https?:\\/\\/(?:www.)?stackoverflow\\.com\\/questions\\/[\\d]+\\/[^\\s#]+#([\\d]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern stackOverflowShortQuestionLinkPattern = Pattern.compile("https?:\\/\\/(?:www.)?stackoverflow\\.com/q/([\\d]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern stackOverflowLongQuestionLinkPattern = Pattern.compile("https?:\\/\\/(?:www.)?stackoverflow\\.com\\/questions\\/([\\d]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern stackOverflowCommentLinkPattern = Pattern.compile("https?:\\/\\/(?:www.)?stackoverflow\\.com\\/(questions\\/\\d+)\\/[^\\s\\/#]+(?:\\/\\d+)?(?:\\?[^\\s\\/#]+)?(#comment\\d+_\\d+)", Pattern.CASE_INSENSITIVE);

    // list downloaded from http://data.iana.org/TLD/tlds-alpha-by-domain.txt
    private static final String topLevelDomainList = "tld-list.txt";
    public static Set<String> validTopLevelDomains = new HashSet<>();

    static {
        urlRegex = encloseInNonCapturingGroup(protocolRegex) + "://" + completeDomainRegex + makeOptional(encloseInNonCapturingGroup(pathRegex)) + makeOptional(encloseInNonCapturingGroup(queryRegex)) + makeOptional(encloseInNonCapturingGroup(fragmentIdentifierRegex));
        urlPattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);

        // pattern to extract the complate domain from a domain string
        completeDomainPattern = Pattern.compile(encloseInCapturingGroup(completeDomainRegex) + "(?:[^\\w\\-.].*)?$");

        // pattern to extract the root domain from a domain string
        rootDomainPattern = Pattern.compile(rootDomainRegex, Pattern.CASE_INSENSITIVE);

        // read list with valid URIs
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(topLevelDomainList)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // ignore comments
                if (line.trim().startsWith("#")) {
                    continue;
                }
                validTopLevelDomains.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public URL(String url) throws MalformedURLException {
        this.urlString = cleanUrl(url);

        if (isEmpty()) {
            return;
        }
        if (!isValid()) {
            throw new MalformedURLException("Malformed URL: " + urlString);
        }

        this.urlObject = new java.net.URL(this.urlString);
        extractURLComponents();
    }

    private String cleanUrl(String url) {
        if (url == null) {
            return null;
        }

        url = url.trim();

        int length = 0;
        while (length != url.length()) {
            length = url.length();
            url = cleanPunctuation(url);
            url = cleanEscapedCharacters(url);
        }

        return url;
    }

    private String cleanPunctuation(String url) {
        while (url.endsWith(".") || url.endsWith(",") || url.endsWith(":") || url.endsWith(";") || url.endsWith("%") || url.endsWith("#")) {
            url = url.substring(0, url.length()-1);
        }
        return url;
    }

    private String cleanEscapedCharacters(String url) {
        while (url.endsWith("&#xA") || url.endsWith("&#xD")) {
            url = url.substring(0, url.length()-4);
        }
        return url;
    }

    private boolean isValid() {
        Matcher urlMatcher = URL.urlPattern.matcher(this.urlString);
        return urlMatcher.matches();
    }

    public boolean isEmpty() {
        return this.urlString == null || this.urlString.length() == 0;
    }

    private void extractURLComponents() throws MalformedURLException {
        if (this.urlObject == null) {
            return;
        }

        this.protocol = this.urlObject.getProtocol();
        this.completeDomain = getCompleteDomain(this.urlObject);
        this.rootDomain = getRootDomain(this.urlObject);
        this.topLevelDomain = getTopLevelDomain(urlObject);

        if (!isValidTopLevelDomain(topLevelDomain)) {
            throw new MalformedURLException("Invalid Top Level Domain: " + topLevelDomain);
        }

        this.path = getPath(this.urlObject);
        this.query = getQuery(this.urlObject);
        this.fragmentIdentifier = getFragmentIdentifier(this.urlObject);
    }

    private boolean isValidTopLevelDomain(String topLevelDomain) {
        return validTopLevelDomains.contains(topLevelDomain.toLowerCase());
    }

    private String getCompleteDomain(java.net.URL url) {
        Matcher completeDomainMatcher = URL.completeDomainPattern.matcher(url.getHost());
        if (!completeDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of complete domain failed for URL: " + url);
        }
        return completeDomainMatcher.group(1);
    }

    private String getRootDomain(java.net.URL url) {
        Matcher rootDomainMatcher = URL.rootDomainPattern.matcher(url.getHost());
        if (!rootDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of root domain failed for URL: " + url);
        }
        return rootDomainMatcher.group(1);
    }

    private String getTopLevelDomain(java.net.URL url) {
        Matcher rootDomainMatcher = URL.rootDomainPattern.matcher(url.getHost());
        if (!rootDomainMatcher.find()) {
            throw new IllegalArgumentException("Extraction of top-level domain failed for URL: " + url);
        }
        return rootDomainMatcher.group(2);
    }

    private String getPath(java.net.URL url) {
        String path = url.getPath();

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

    private String getQuery(java.net.URL url) {
        String query = url.getQuery();
        if (query != null && query.trim().length() == 0) {
            query = null;
        }
        return query;
    }

    private String getFragmentIdentifier(java.net.URL url) {
        String fragmentIdentifier = url.getRef();
        if (fragmentIdentifier != null && fragmentIdentifier.trim().length() == 0) {
            fragmentIdentifier = null;
        }
        return fragmentIdentifier;
    }

    public String getUrlString() {
        return urlString;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getCompleteDomain() {
        return completeDomain;
    }

    public String getRootDomain() {
        return rootDomain;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getFragmentIdentifier() {
        return fragmentIdentifier;
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

    public static URL stackOverflowLinkFromSourceCodeLine(String line) {
        Matcher stackOverflowMatcher = stackOverflowLinkPattern.matcher(line);
        String url = "";
        if (stackOverflowMatcher.find()) {
            try {
                url = stackOverflowMatcher.group(1);
                return new URL(url);
            } catch (MalformedURLException eOutter) {
                // Java's URL class doesn't accept SO search URLs like https://stackoverflow.com/search?q=user:9841338+[python]
                // This workaround ignores everything after the problematic colon character
                Matcher stackOverflowSearchMatcher = stackOverflowSearchLinkPattern.matcher(url);
                if (stackOverflowSearchMatcher.find()) {
                    try {
                        return new URL(stackOverflowSearchMatcher.group(1));
                    } catch (MalformedURLException eInner) {
                        logger.warning(eInner.toString());
                    }
                } else {
                    logger.warning(eOutter.toString());
                }
                return null;
            }
        }
        logger.info("No Stack Overflow link found in: " + line);
        return null;
    }

    public static URL getNormalizedStackOverflowLink(URL url) {
        try {
            Matcher commentMatcher = stackOverflowCommentLinkPattern.matcher(url.getUrlString());
            if (commentMatcher.find()) {
                return new URL("https://stackoverflow.com/" + commentMatcher.group(1)
                        + commentMatcher.group(2).toLowerCase());
            }

            Matcher shortAnswerMatcher = stackOverflowShortAnswerLinkPattern.matcher(url.getUrlString());
            if (shortAnswerMatcher.find()) {
                return new URL("https://stackoverflow.com/a/" + shortAnswerMatcher.group(1));
            }

            Matcher longAnswerMatcher = stackOverflowLongAnswerLinkPattern.matcher(url.getUrlString());
            if (longAnswerMatcher.find()) {
                return new URL("https://stackoverflow.com/a/" + longAnswerMatcher.group(1));
            }

            Matcher shortQuestionMatcher = stackOverflowShortQuestionLinkPattern.matcher(url.getUrlString());
            if (shortQuestionMatcher.find()) {
                return new URL("https://stackoverflow.com/q/" + shortQuestionMatcher.group(1));
            }

            Matcher longQuestionMatcher = stackOverflowLongQuestionLinkPattern.matcher(url.getUrlString());
            if (longQuestionMatcher.find()) {
                return new URL("https://stackoverflow.com/q/" + longQuestionMatcher.group(1));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        logger.info("Normalization of link failed: " + url.getUrlString());
        return null;
    }
}
