import java.util.Objects;

class Card { 
    private String rank; 
    private String suit; 
    
    public Card(String rank, String suit) { 
        this.rank = rank; 
        this.suit = suit; 
    } 
    
    @Override 
    public String toString() { 
        return rank + " of " + suit; 
    } 
    
    @Override 
    public boolean equals(Object obj) { 
        if (this == obj) return true; 
        if (!(obj instanceof Card)) return false; 
        Card c = (Card) obj; 
        return rank.equals(c.rank) && suit.equals(c.suit); 
    } 
    
    @Override 
    public int hashCode() { 
        return Objects.hash(rank, suit); 
    } 
} 

public class Practical3_2_Cards { 
    public static void main(String[] args) { 
        Card[] cards = new Card[5]; 
        cards[0] = new Card("Ace", "Spades"); 
        cards[1] = new Card("King", "Hearts"); 
        cards[2] = new Card("Queen", "Diamonds"); 
        cards[3] = new Card("Ace", "Spades"); 
        cards[4] = new Card("Jack", "Clubs"); 
        
        for (int i = 0; i < cards.length; i++) { 
            for (int j = 0; j < i; j++) { 
                if (cards[i].equals(cards[j])) { 
                    System.out.println("Duplicate found: " + cards[i]); 
                    return; 
                } 
            } 
        } 
    } 
}
