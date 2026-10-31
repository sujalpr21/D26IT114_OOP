import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class Practical12_Collections {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("PART A: PRACTICE PROGRAMS (LAB-NN)");
        System.out.println("==========================================");
        System.out.println("\n--- Program 1: Word Frequency Counter ---");
        String paragraph = "Java is powerful and Java is popular. Java developers love Java code.";
        System.out.println("Input Paragraph:\n\"" + paragraph + "\"");
        String[] words = paragraph.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "").split("\\s+");
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String word : words) {
            frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
        }
       
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(frequencyMap.entrySet());
        entryList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        System.out.println("\nWords Sorted by Frequency (Highest First):");
        for (Map.Entry<String, Integer> entry : entryList) {
            System.out.printf(" %-12s : %d%n", entry.getKey(), entry.getValue());
        }
        
        System.out.println("\n--- Program 2: Unique Visitors Tracker ---");
        List<String> logins = Arrays.asList("alice", "bob", "charlie", "alice", "david", "bob", "eve", "charlie");
        System.out.println("Logins Log: " + logins);
        HashSet<String> uniqueHashSet = new HashSet<>(logins);
        LinkedHashSet<String> uniqueLinkedHashSet = new LinkedHashSet<>(logins);
        System.out.println("Total Unique Users Count: " + uniqueHashSet.size());
        System.out.print("First 3 Distinct Users (in order of first appearance): ");
        Iterator<String> iterator = uniqueLinkedHashSet.iterator();
        List<String> firstThree = new ArrayList<>();
        int count = 0;
        while (iterator.hasNext() && count < 3) {
            firstThree.add(iterator.next());
            count++;
        }
        System.out.println(firstThree);
        
        System.out.println("\n--- Program 3: Leaderboard (Top-N) ---");
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 1200);
        scores.put("Bob", 1500);
        scores.put("Charlie", 1500); 
        scores.put("David", 900);
        scores.put("Eve", 1800);
        scores.put("Frank", 1200);  
        System.out.println("Raw Player Scores: " + scores);
        int N = 4;
        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scores.entrySet());
        
        leaderboard.sort((e1, e2) -> {
            int scoreCompare = e2.getValue().compareTo(e1.getValue());
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return e1.getKey().compareTo(e2.getKey());
        });
        System.out.println("\nTop " + N + " Leaderboard:");
        System.out.println("-------------------------");
        System.out.println("Rank   | Player     | Score");
        System.out.println("-------------------------");
        for (int i = 0; i < Math.min(N, leaderboard.size()); i++) {
            Map.Entry<String, Integer> entry = leaderboard.get(i);
            System.out.printf(" #%-4d | %-10s | %d%n", (i + 1), entry.getKey(), entry.getValue());
        }
    }
}
