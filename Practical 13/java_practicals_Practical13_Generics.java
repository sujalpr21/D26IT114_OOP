import java.util.Arrays;
import java.util.EmptyStackException;

public class Practical13_Generics {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("PART A: PRACTICE PROGRAMS (LAB-NN)");
        System.out.println("==========================================");
        System.out.println("\n--- Program 1: Generic Containers & Method ---");
        
        Box<String> stringBox = new Box<>();
        stringBox.set("Java Generics");
        String stringValue = stringBox.get(); 
        System.out.println("Box<String> value: " + stringValue);
        Box<Integer> intBox = new Box<>();
        intBox.set(100);
        Integer intValue = intBox.get(); 
        System.out.println("Box<Integer> value: " + intValue);
        
        Pair<String, Double> accountBalance = new Pair<>("ACC-101", 1550.75);
        System.out.println("Pair<String, Double>: Key=" + accountBalance.getFirst() + ", Value=$" + accountBalance.getSecond());
        
        String[] names = {"Alice", "Bob", "Charlie"};
        Integer[] numbers = {42, 88, 99};
        
        System.out.println("First element of String array: " + getFirstElement(names));
        System.out.println("First element of Integer array: " + getFirstElement(numbers));
        
        System.out.println("\n--- Program 2: Bounded Stats Method ---");
        Integer[] intArr = {10, 20, 30, 40};
        Double[] doubleArr = {2.5, 3.5, 4.0};
        Long[] longArr = {100L, 200L, 300L};
        System.out.println("Average of Integer[]: " + average(intArr));
        System.out.println("Average of Double[]:  " + average(doubleArr));
        System.out.println("Average of Long[]:    " + average(longArr));
        System.out.println("\nNote on Type Safety:");
        System.out.println(" String[] strArr = {\"a\", \"b\"};");
        System.out.println(" average(strArr); // COMPILE ERROR: String does not extend java.lang.Number");
        
        System.out.println("\n--- Program 3: Generic Array-Backed Stack ---");
        System.out.println("1. Testing ArrayStack<Integer>:");
        ArrayStack<Integer> numberStack = new ArrayStack<>(3);
        numberStack.push(10);
        numberStack.push(20);
        numberStack.push(30);
        System.out.println(" Popped: " + numberStack.pop());
        System.out.println(" Peek top: " + numberStack.peek());
        System.out.println(" Current Size: " + numberStack.size());
        
        System.out.println("\n2. Testing ArrayStack<String>:");
        ArrayStack<String> textStack = new ArrayStack<>(2);
        textStack.push("First");
        textStack.push("Second");
        System.out.println(" Popped: " + textStack.pop());
        System.out.println(" Popped: " + textStack.pop());
        
        System.out.println("\n3. Handling Empty Case:");
        try {
            System.out.println(" Attempting peek on empty stack...");
            textStack.peek();
        } catch (EmptyStackException e) {
            System.out.println(" Caught Exception: EmptyStackException (Empty case handled safely!)");
        }
    }

    public static <E> E getFirstElement(E[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[0];
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N val : values) {
            sum += val.doubleValue(); 
        }
        return sum / values.length;
    }
}

class Box<T> {
    private T item;
    public void set(T item) {
        this.item = item;
    }
    public T get() {
        return item;
    }
}

class Pair<A, B> {
    private A first;
    private B second;
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    public A getFirst() {
        return first;
    }
    public B getSecond() {
        return second;
    }
}

class ArrayStack<E> {
    private Object[] elements;
    private int size = 0;
    public ArrayStack(int capacity) {
        elements = new Object[capacity];
    }
    public void push(E element) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
        elements[size++] = element;
    }
    @SuppressWarnings("unchecked")
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        E value = (E) elements[--size];
        elements[size] = null; 
        return value;
    }
    @SuppressWarnings("unchecked")
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return (E) elements[size - 1];
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }
}
