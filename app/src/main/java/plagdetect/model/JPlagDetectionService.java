package plagdetect.model;

import de.jplag.JPlag;
import de.jplag.JPlagResult;
import de.jplag.options.JPlagOptions;
import de.jplag.java.JavaLanguage;
import de.jplag.reporting.reportobject.ReportObjectFactory;

import java.io.File;
import java.util.Set;

public class JPlagDetectionService {

    public static JPlagResult runDetection(String submissionDirPath, String resultDirPath) throws Exception {
        File submissionDir = new File(submissionDirPath);
        File resultDir = new File(resultDirPath);

        if (!submissionDir.exists() || !submissionDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid submission directory: " + submissionDirPath);
        }

        JPlagOptions options = new JPlagOptions(new JavaLanguage(), Set.of(submissionDir), Set.of(resultDir));

        JPlagResult result = JPlag.run(options);

        // Save report
        File resultZip = new File(resultDir, "results.zip");
        ReportObjectFactory factory = new ReportObjectFactory(resultZip);
        factory.createAndSaveReport(result);

        return result;
    }
}
