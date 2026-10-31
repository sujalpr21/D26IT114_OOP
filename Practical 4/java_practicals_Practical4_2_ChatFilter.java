import java.util.Scanner;

class ChatFilter {
    public static String filterLogs(String[] logs, String keyword) { 
        StringBuilder report = new StringBuilder();
        int count = 0;
        for (String line : logs) {
            String[] parts = line.split(" ", 3);
            if (parts.length < 3) {
                continue;
            }
            String time = parts[0]; 
            String user = parts[1]; 
            String message = parts[2];

            if (message.toLowerCase().contains(keyword.toLowerCase())) {
                count++; 
                report.append(time)
                      .append(" ")
                      .append(user)
                      .append(": ")
                      .append(message)
                      .append("\n");
            }
        }
        return "Matches: " + count + "\n" + report.toString();
    }
}

public class Practical4_2_ChatFilter {
    public static void main(String[] args) {
        String[] logs = {
            "10:05 alice Hello there", "10:10 bob How are you?", "10:15 charlie Good morning", "10:20"
        };
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter keyword: "); 
        String keyword = sc.nextLine();
        String result = ChatFilter.filterLogs(logs, keyword);
        System.out.println(result);
        sc.close();
    }
}
