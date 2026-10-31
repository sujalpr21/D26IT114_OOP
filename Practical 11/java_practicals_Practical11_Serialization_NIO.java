import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class Practical11_Serialization_NIO {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("LAB PRACTICE: SERIALIZATION, FILES & NIO");
        System.out.println("==========================================");
        
        System.out.println("\n--- Program 1: Object Serialization & Deserialization ---");
        String serialFile = "user_sessions.ser";
        UserSession[] originalSessions = {
            new UserSession(101, "Alice", "secret_pass_123"),
            new UserSession(102, "Bob", "my_secure_pwd")
        };
        System.out.println("Original Objects before Saving:");
        for (UserSession u : originalSessions) {
            System.out.println(" " + u);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serialFile))) {
            oos.writeObject(originalSessions);
            System.out.println("\n[Success] Array of UserSession objects written to " + serialFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serialFile))) {
            UserSession[] restoredSessions = (UserSession[]) ois.readObject();
            System.out.println("\nRestored Objects after Deserialization:");
            for (UserSession u : restoredSessions) {
                System.out.println(" " + u);
            }
            System.out.println(" Notice: 'sessionToken' (transient) was NOT saved and restored as null!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { Files.deleteIfExists(Paths.get(serialFile)); } catch (IOException ignored) {}
        }
        
        System.out.println("\n--- Program 2: Log Analyzer ---");
        Path logDir = Paths.get("logs_temp");
        String keyword = "ERROR";
        try {
            Files.createDirectories(logDir);
            Path log1 = logDir.resolve("app.log");
            Path log2 = logDir.resolve("db.log");
            Files.write(log1, List.of("INFO: Application started", "ERROR: Database connection failed", "INFO: Retrying..."));
            Files.write(log2, List.of("INFO: Query executed", "ERROR: Timeout on table read", "ERROR: Memory leak detected"));
            Path[] logFiles = {log1, log2};
            long totalLinesAllFiles = 0;
            long keywordCountAllFiles = 0;
            for (Path p : logFiles) {
                List<String> lines = Files.readAllLines(p);
                long fileLineCount = lines.size();
                long fileKeywordCount = lines.stream().filter(line -> line.contains(keyword)).count();
                BasicFileAttributes attrs = Files.readAttributes(p, BasicFileAttributes.class);
                System.out.println("File: " + p.getFileName() + " | Size: " + attrs.size() + " bytes | Lines: " + fileLineCount + " | Contains '" + keyword + "': " + fileKeywordCount);
                totalLinesAllFiles += fileLineCount;
                keywordCountAllFiles += fileKeywordCount;
            }
            System.out.println("\nSummary Across All Files:");
            System.out.println(" Total Lines Read: " + totalLinesAllFiles);
            System.out.println(" Total Occurrences of '" + keyword + "': " + keywordCountAllFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n--- Program 3: Folder Tree Report Generator ---");
        Path reportFile = Paths.get("directory_report.txt");
        try {
            List<String> reportLines = new ArrayList<>();
            reportLines.add("=========================================");
            reportLines.add("DIRECTORY TREE REPORT: " + logDir.toAbsolutePath());
            reportLines.add("=========================================");
            walkDirectory(logDir, reportLines);
            Files.write(reportFile, reportLines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Generated Directory Report at '" + reportFile.toAbsolutePath() + "':\n");
            Files.readAllLines(reportFile).forEach(line -> System.out.println(" " + line));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(logDir.resolve("app.log"));
                Files.deleteIfExists(logDir.resolve("db.log"));
                Files.deleteIfExists(logDir);
                Files.deleteIfExists(reportFile);
            } catch (IOException ignored) {}
        }
    }
    
    private static void walkDirectory(Path currentDir, List<String> report) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {
            for (Path entry : stream) {
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                if (attrs.isDirectory()) {
                    report.add("[DIR]  " + entry.getFileName());
                    walkDirectory(entry, report);
                } else {
                    FileTime modifiedTime = attrs.lastModifiedTime();
                    report.add(String.format("[FILE] %-12s | Size: %-6d bytes | Last Modified: %s",
                            entry.getFileName(), attrs.size(), modifiedTime));
                }
            }
        }
    }
}

class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userId;
    private String username;
    private transient String sessionToken;

    public UserSession(int userId, String username, String sessionToken) {
        this.userId = userId;
        this.username = username;
        this.sessionToken = sessionToken;
    }
    
    @Override
    public String toString() {
        return "UserSession{userId=" + userId + ", username='" + username + "', sessionToken='" + sessionToken + "'}";
    }
}
