package hms;

public class DeluxeSingle extends Room {
    public DeluxeSingle(String roomID, String status) {
        super(roomID, status);
    }

    public String getName() { return "Deluxe Single"; }
    public double getPriceRate() { return 39.99; }
}
