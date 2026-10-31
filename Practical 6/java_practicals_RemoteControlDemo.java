interface Switchable { 
    void on(); 
    void off(); 
 
    default void toggle() { 
        on(); 
    } 
} 
 
class Fan implements Switchable { 
    public void on() { 
        System.out.println("Fan is ON"); 
    } 
 
    public void off() { 
        System.out.println("Fan is OFF"); 
    } 
} 
 
class Light implements Switchable { 
    public void on() { 
        System.out.println("Light is ON"); 
    } 
 
    public void off() { 
        System.out.println("Light is OFF"); 
    } 
} 
 
interface SwitchRule { 
    boolean maySwitchOn(Switchable device, int hour); 
} 
 
public class RemoteControlDemo { 
    public static void main(String[] args) { 
        Switchable[] devices = { 
            new Fan(), 
            new Light() 
        };
        System.out.println("Toggling devices:"); 
 
        for (Switchable device : devices) { 
            device.toggle(); 
        } 
 
        SwitchRule rule1 = new SwitchRule() { 
            public boolean maySwitchOn(Switchable device, int hour) { 
                return hour >= 8 && hour <= 22; 
            } 
        }; 
 
        SwitchRule rule2 = (device, hour) -> hour >= 6 && hour <= 23; 
 
        int hour = 20; 
 
        System.out.println("\nUsing anonymous class:"); 
        System.out.println("Can switch ON at " + hour + ":00? " + rule1.maySwitchOn(devices[0], hour)); 
 
        System.out.println("\nUsing lambda:"); 
        System.out.println("Can switch ON at " + hour + ":00? " + rule2.maySwitchOn(devices[1], hour)); 
    } 
}
