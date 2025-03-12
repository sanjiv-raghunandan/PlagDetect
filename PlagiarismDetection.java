import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;


public class PlagiarismDetection{
    private static final String SUB_DIR = "submissions";
    private static final String RES_DIR = "results";

    public static void main(String[] args){
        try{
            Files.createDirectories(Paths.get(RES_DIR)); //create results directory
            List<File> submissions = getSubmissions();//get all files in submissions directory
            
            if (submissions.isEmpty()) {
                System.out.println("No submissions found.");
                return;
            }
            
            System.out.println("Found " + submissions.size() + " submissions.");
        }
        catch(Exception e){
            System.err.println("Error:"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<File> getSubmissions() throws IOException {
        Path subPath = Paths.get(SUB_DIR);

        if (!Files.exists(subPath)){
            System.err.println("Submissions directory does not exist");
            return new ArrayList<>();
        }

        return Files.list(subPath)
            .filter(Files::isRegularFile)
            .map(Path::toFile)
            .collect(Collectors.toList());
    }
}
