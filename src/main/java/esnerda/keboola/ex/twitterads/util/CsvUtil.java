package esnerda.keboola.ex.twitterads.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * @author David Esner
 */
public class CsvUtil {

	public static void deleteEmptyFiles(List<File> files) {
		for (File f : files) {
			try {
				if (isFileEmpty(f)) {
					f.delete();
				}
			} catch (IOException e) {
				// do nothing, I really dont care here
			}
		}
	}

	public static boolean isFileEmpty(File f) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line = br.readLine();
			return StringUtils.isBlank(line);
		}
	}

}
