package de.unitrier.st.util;

import java.util.regex.Pattern;

public class Patterns {
    // for the basic regex, see https://stackoverflow.com/a/6041965, alternative: https://stackoverflow.com/a/29288898
    // see also https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
    public static final Pattern url = Pattern.compile("(?:https?|ftp)://(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))(?:/[\\w.,@^=%&:/~+-]+)?(?:\\?[\\w.,@?^=%&:/~+#-]+)?(?:#[\\w.,@?^=%&:/~+#-]+)?");

    // pattern to extract protocol from URL
    public static final Pattern protocol = Pattern.compile("^(https?|ftp)");

    // pattern to extract domain (including subdomains) from URL
    public static final Pattern completeDomain = Pattern.compile("(?:https?|ftp)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))");

    // pattern to extract root domain from domain string
    public static final Pattern rootDomain = Pattern.compile("([\\w_-]+\\.[\\w_-]+)$");

    // pattern to extract path from URL
    public static final Pattern path = Pattern.compile("(?:https?|ftp)://(?:[\\w_-]+(?:(?:\\.[\\w_-]+)+))/([\\w.,@^=%&:/~+-]+)");
}
