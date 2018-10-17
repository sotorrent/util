import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.sotorrent.util.URL.validTopLevelDomains;

class URLTest {
    @Test
    void testLoadingTldList(){
        assertNotNull(validTopLevelDomains);
        assertTrue(validTopLevelDomains.size() > 0);
    }
}
