import org.junit.jupiter.api.Test;
import org.sotorrent.util.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sotorrent.util.URL.validTopLevelDomains;

class URLTest {
    private String[] stackOverflowAnswerLinkVariants = {
            "https://stackoverflow.com/a/53022912",
            "HTTPS://STACKOVERFLOW.COM/A/53022912",
            "http://stackoverflow.com/a/53022912",
            "https://www.stackoverflow.com/a/53022912",
            "https://stackoverflow.com/questions/53022815/running-python-function-in-ansible/53022912#53022912",
            "https://stackoverflow.com/questions/53022815/running-python-function-in-ansible/53022912"
    };

    private String[] stackOverflowQuestionLinkVariants = {
            "https://stackoverflow.com/questions/52992319/how-to-format-currency-number-in-ireport-with-the-currency-simbol-at-left-and-th",
            "https://stackoverflow.com/questions/52992319",
            "https://stackoverflow.com/q/53022815",
            "HTTPS://STACKOVERFLOW.COM/Q/53022815",
            "http://stackoverflow.com/q/53022815",
            "http://www.stackoverflow.com/q/53022815",
    };

    private String[] stackOverflowCommentLinkVariants = {
            "https://stackoverflow.com/questions/52761212/how-can-you-merge-objects-in-array-of-objects#comment92462603_52761348",
            "http://stackoverflow.com/questions/52761212/how-can-you-merge-objects-in-array-of-objects#comment92462603_52761348",
            "https://www.stackoverflow.com/questions/52761212/how-can-you-merge-objects-in-array-of-objects#comment92462603_52761348",
            "HTTPS://WWW.STACKOVERFLOW.COM/QUESTIONS/52761212/how-can-you-merge-objects-in-array-of-objects#COMMENT92462603_52761348"
    };

    private String[] stackOverflowNonPostLinkVariants = {
            "https://stackoverflow.com/users/9841338/alexis",
            "https://stackoverflow.com/search?q=user:9841338+[python]",
            "https://stackoverflow.com/questions/tagged/javascript"
    };

    private String[] stackOverflowBrokenQuestionLinks = {
            // see https://github.com/sotorrent/db-scripts/issues/2
            // see https://raw.githubusercontent.com/GEKONavsat/FreeIMU/master/libraries/FreeIMU/debug/decode_float.py
            "http://stackoverflow.com/questions/4315190/single-precision-big-endian-f...",
            "# http://stackoverflow.com/questions/4315190/single-precision-big-endian-f..."
    };

    private String[] stackOverflowBrokenAnswerLinks = {
            // see https://github.com/sotorrent/db-scripts/issues/2
            // see https://raw.githubusercontent.com/GEKONavsat/FreeIMU/master/libraries/FreeIMU/debug/decode_float.py
            // The following two links point to the wrong answer (post id 1), because of the abbreviation.
            // Since there exist one-digit post ids (e.g., stackoverflow.com/a/6), we can't fix this without
            // compromising on the matching in other scenarios.
            "http://stackoverflow.com/questions/1592158/python-convert-hex-to-float/1...",
            "# http://stackoverflow.com/questions/1592158/python-convert-hex-to-float/1..."
    };

    @Test
    void testLoadingTldList(){
        assertNotNull(validTopLevelDomains);
        assertTrue(validTopLevelDomains.size() > 0);
    }

    @Test
    void testStackOverFlowLinkFromSourceCodeLine() {
        for (String link : stackOverflowAnswerLinkVariants) {
            testNormalization(link, 'a');
        }

        for (String link : stackOverflowBrokenAnswerLinks) {
            testNormalization(link, 'a');
        }

        for (String link : stackOverflowQuestionLinkVariants) {
            testNormalization(link, 'q');
        }

        for (String link : stackOverflowBrokenQuestionLinks) {
            testNormalization(link, 'q');
        }

        for (String link : stackOverflowCommentLinkVariants) {
            URL url = URL.stackOverflowLinkFromSourceCodeLine(link);
            assertNotNull(url);
            URL normalizedUrl = URL.getNormalizedStackOverflowLink(url);
            assertNotNull(normalizedUrl);
            assertTrue(normalizedUrl.getUrlString().toLowerCase().contains("#comment"));
        }

        for (String link : stackOverflowNonPostLinkVariants) {
            URL url = URL.stackOverflowLinkFromSourceCodeLine(link);
            assertNotNull(url);
            URL normalizedUrl = URL.getNormalizedStackOverflowLink(url);
            assertNull(normalizedUrl);
        }
    }

    private void testNormalization(String link, char type) {
        if (type != 'a' && type != 'q') {
            return;
        }
        URL url = URL.stackOverflowLinkFromSourceCodeLine(link);
        assertNotNull(url);
        URL normalizedUrl = URL.getNormalizedStackOverflowLink(url);
        assertNotNull(normalizedUrl);
        assertTrue(normalizedUrl.getUrlString().startsWith("https://stackoverflow.com/" + type +"/"));
    }
}
