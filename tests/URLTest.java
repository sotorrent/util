import org.junit.jupiter.api.Test;
import org.sotorrent.util.URL;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class URLTest {
    private String[] stackOverflowAnswerLinkVariants = {
            "https://stackoverflow.com/a/53022912",
            "HTTPS://STACKOVERFLOW.COM/A/53022912",
            "http://stackoverflow.com/a/53022912",
            "https://www.stackoverflow.com/a/53022912",
            "https://stackoverflow.com/questions/53022815/running-python-function-in-ansible/53022912#53022912",
            "http://stackoverflow.com/a/3758880/1035417"
    };

    private String[] stackOverflowQuestionLinkVariants = {
            "https://stackoverflow.com/questions/52992319/how-to-format-currency-number-in-ireport-with-the-currency-simbol-at-left-and-th",
            "https://stackoverflow.com/questions/52992319",
            "https://stackoverflow.com/q/53022815",
            "HTTPS://STACKOVERFLOW.COM/Q/53022815",
            "http://stackoverflow.com/q/53022815",
            "http://www.stackoverflow.com/q/53022815",
            "https://stackoverflow.com/questions/53022815/running-python-function-in-ansible/53022912" // this points to the question, not the answer
    };

    Pattern stackOverflowNormalizedCommentLinkPattern = Pattern.compile("(https?:\\/\\/(?:www.)?stackoverflow\\.com\\/questions\\/\\d+#comment\\d+_\\d+)", Pattern.CASE_INSENSITIVE);
    private String[] stackOverflowCommentLinkVariants = {
            "https://stackoverflow.com/questions/52761212/how-can-you-merge-objects-in-array-of-objects#comment92462603_52761348",
            "http://stackoverflow.com/questions/52761212/how-can-you-merge-objects-in-array-of-objects#comment92462603_52761348",
            "https://www.stackoverflow.com/questions/52761212/how-can-you-merge-objects-in-array-of-objects#comment92462603_52761348",
            "HTTPS://WWW.STACKOVERFLOW.COM/QUESTIONS/52761212/how-can-you-merge-objects-in-array-of-objects#COMMENT92462603_52761348",
            "https://stackoverflow.com/questions/28705447/is-there-a-java-method-that-fills-a-list-by-calling-a-function-many-times/28705651?noredirect=1#comment45733057_28705651"
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
            "# http://stackoverflow.com/questions/4315190/single-precision-big-endian-f...",
            "http://stackoverflow.com/questions/1592158/python-convert-hex-to-float/1...",
            "# http://stackoverflow.com/questions/1592158/python-convert-hex-to-float/1..."
    };

    private String[] stackOverflowLinkEndings = {
            "http://stackoverflow.com/q/1480971/3191,",
            "http://stackoverflow.com/questions/7050137/\\",
            "http://stackoverflow.com/questions/13350577/can-powershell-get-childproperty-get-a-list-of-real-registry-keys-like-reg-query</maml:uri>",
            "http://stackoverflow.com/questions/20522870/about-the-dynamic-de-optimization-of-hotspot&lt;",
            "http://stackoverflow.com/users/4759300/develost'",
            "http://stackoverflow.com/questions/21557461/execute-a-batch-file-from-nodejs|stackoverflow}",
            "http://stackoverflow.com/questions/3721249/python-date-interval-intersection'''",
            "http://stackoverflow.com/questions/35151624/underscores-font-size-mixin>\\n\\t$rem-size:"
    };

    @Test
    void testLoadingTldList(){
        assertNotNull(URL.getValidTopLevelDomains());
        assertTrue(URL.getValidTopLevelDomains().size() > 0);
    }

    @Test
    void testStackOverFlowLinkFromSourceCodeLine() {
        for (String link : stackOverflowAnswerLinkVariants) {
            testNormalization(link, 'a');
        }

        for (String link : stackOverflowQuestionLinkVariants) {
            testNormalization(link, 'q');
        }

        for (String link : stackOverflowBrokenQuestionLinks) {
            testNormalization(link, 'q');
        }

        for (String link : stackOverflowCommentLinkVariants) {
            testNormalization(link, 'c', true);
        }

        for (String link : stackOverflowNonPostLinkVariants) {
            URL url = URL.stackOverflowLinkFromSourceCodeLine(link);
            assertNotNull(url);
            URL normalizedUrl = URL.getNormalizedStackOverflowLink(url);
            assertNull(normalizedUrl);
        }

        for (String link : stackOverflowLinkEndings) {
            URL url = URL.stackOverflowLinkFromSourceCodeLine(link);
            assertNotNull(url);
            assertTrue(url.getUrlString().matches(".+[\\w/]$"), url.getUrlString());
        }
    }

    private void testNormalization(String link, char type) {
        testNormalization(link, type, false);
    }

    private void testNormalization(String link, char type, boolean isComment) {
        if (type != 'a' && type != 'q' && type != 'c') {
            return;
        }
        URL url = URL.stackOverflowLinkFromSourceCodeLine(link);
        assertNotNull(url);

        URL normalizedUrl = URL.getNormalizedStackOverflowLink(url);
        assertNotNull(normalizedUrl);

        if (isComment) {
            assertTrue(normalizedUrl.getUrlString().startsWith("https://stackoverflow.com/"), normalizedUrl.getUrlString());
            assertTrue(normalizedUrl.getUrlString().contains("#comment"), normalizedUrl.getUrlString());
            assertTrue(stackOverflowNormalizedCommentLinkPattern.matcher(normalizedUrl.getUrlString()).matches());
        } else {
            assertTrue(normalizedUrl.getUrlString().startsWith("https://stackoverflow.com/" + type +"/"), normalizedUrl.getUrlString());
        }
    }
}
