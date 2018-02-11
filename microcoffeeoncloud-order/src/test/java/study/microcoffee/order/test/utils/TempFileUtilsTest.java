package study.microcoffee.order.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;

import org.junit.Test;

/**
 * Unit tests of {@link TempFileUtils}.
 */
public class TempFileUtilsTest {

    private static final String TESTDATA_FILE = "testdata.txt";

    @Test
    public void putInTempFileShouldCreateTempFile() throws Exception {
        String text = "The quick brown fox jumped over the lazy dog.";
        Files.write(new File("target/test-classes/" + TESTDATA_FILE).toPath(), text.getBytes());

        URL fileUrl = TempFileUtilsTest.class.getClassLoader().getResource(TESTDATA_FILE);

        File tempFile = TempFileUtils.putInTempFile(fileUrl, "temp");

        String readBackText = new String(Files.readAllBytes(tempFile.toPath()));
        assertThat(readBackText).isEqualTo(text);
    }
}
