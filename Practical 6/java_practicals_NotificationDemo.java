@FunctionalInterface 
interface Notifier { 
    void send(String message); 
} 

interface Urgent { 
} 
 
class EmailNotifier implements Notifier, Urgent { 
    public void send(String message) { 
        System.out.println("Email: " + message); 
    } 
} 
 
class SMSNotifier implements Notifier { 
    public void send(String message) { 
        System.out.println("SMS: " + message); 
    } 
}

class UrgentSMSNotifier implements Notifier, Urgent { 
    public void send(String message) { 
        System.out.println("Urgent SMS: " + message); 
    } 
} 
 
public class NotificationDemo { 
    public static void main(String[] args) { 
        Notifier email = message -> System.out.println("Email: " + message); 
        Notifier sms = message -> System.out.println("SMS: " + message); 
 
        Notifier[] senders = { 
            email, 
            sms, 
            new EmailNotifier(), 
            new UrgentSMSNotifier() 
        }; 
 
        String message = "Your order has been shipped."; 
 
        System.out.println("Broadcasting message:\n"); 
 
        for (Notifier sender : senders) { 
            sender.send(message); 
 
            if (sender instanceof Urgent) { 
                sender.send(message); 
            } 
        } 
    } 
}
