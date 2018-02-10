package study.microcoffee.order.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Utility class providing temporary file support.
 */
public abstract class TempFileUtils {

    /**
     * Reads data from a URL and writes it to a temporary file of name <code>&lt;namePrefix&gt;&lt;currentTimeMillis&gt;.tmp</code>.
     *
     * @param url
     *            the input URL to read data from.
     * @param namePrefix
     *            the text to use as file name prefix.
     * @return File object of the temporary file.
     * @throws IOException
     *             if an I/O error occurs when reading or writing.
     */
    public static File putInTempFile(URL url, String namePrefix) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("Url is null. Nothing to write to temporary file.");
        }
        File tempFile = File.createTempFile(namePrefix + System.currentTimeMillis(), ".tmp");
        tempFile.deleteOnExit();

        InputStream input = null;

        try {
            input = url.openStream();
            Files.copy(input, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        return tempFile;
    }
}
