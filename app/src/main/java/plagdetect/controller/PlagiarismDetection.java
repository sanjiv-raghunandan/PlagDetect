// package plagdetect.controller;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;
// import java.util.Map;
// import java.util.HashMap;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.nio.file.Path;
// import java.nio.file.attribute.BasicFileAttributes;
// import java.io.File;
// import java.io.IOException;

// public class PlagiarismDetection {
//     private static final String SUB_DIR = "submissions";
//     private static final String RES_DIR = "results";

//     public static void main(String[] args) {
//         try {
//             Files.createDirectories(Paths.get(RES_DIR)); // create results directory
//             List<File> submissions = getSubmissions(); // get all files in submissions directory
            
//             if (submissions.isEmpty()) {
//                 System.out.println("No submissions found.");
//                 return;
//             }
            
//             System.out.println("Found " + submissions.size() + " submissions.");

//             List<Map<String, Object>> fileMetadata = extractFileMetadata(submissions);
            
//             sendToDatabase(fileMetadata);
//         }
//         catch(Exception e) {
//             System.err.println("Error: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     public static List<File> getSubmissions() throws IOException {
//         Path subPath = Paths.get(SUB_DIR);

//         if (!Files.exists(subPath)) {
//             System.err.println("Submissions directory does not exist");
//             return new ArrayList<>();
//         }

//         return Files.list(subPath)
//             .filter(Files::isRegularFile)
//             .map(Path::toFile)
//             .collect(Collectors.toList());
//     }

//     public static List<Map<String, Object>> extractFileMetadata(List<File> files) throws IOException {
//         List<Map<String, Object>> metadataList = new ArrayList<>();
        
//         for (File file : files) {
//             Map<String, Object> metadata = new HashMap<>();
//             Path filePath = file.toPath();
//             BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            
//             // Basic file information
//             metadata.put("fileName", file.getName());
//             metadata.put("filePath", file.getAbsolutePath());
//             metadata.put("fileSize", file.length());
//             metadata.put("fileExtension", getFileExtension(file.getName()));
            
//             // Time information
//             metadata.put("creationTime", attrs.creationTime().toMillis());
//             metadata.put("lastModifiedTime", attrs.lastModifiedTime().toMillis());
//             metadata.put("lastAccessTime", attrs.lastAccessTime().toMillis());
            
//             // Content information
//             String content = new String(Files.readAllBytes(filePath));
//             metadata.put("lineCount", countLines(content));
//             metadata.put("characterCount", content.length());
            
//             // Add the content for plagiarism detection
//             metadata.put("content", content);
            
//             metadataList.add(metadata);
//         }
        
//         return metadataList;
//     }
    
    
//     // Send metadata to the database
//     private static void sendToDatabase(List<Map<String, Object>> metadataList) {
//         // TODO: Implement database connection and storage
//         System.out.println("Sending " + metadataList.size() + " records to database...");
//         System.out.println("Metadata ready for database submission.");
//     }
    
//     // Get file extension from filename
//     private static String getFileExtension(String fileName) {
//         int lastDotIndex = fileName.lastIndexOf('.');
//         if (lastDotIndex > 0) {
//             return fileName.substring(lastDotIndex + 1);
//         }
//         return "";
//     }
    
//     // Count the number of lines in a string
//     private static int countLines(String text) {
//         if (text.isEmpty()) {
//             return 0;
//         }
//         return text.split("\r\n|\r|\n").length;
//     }
// }
