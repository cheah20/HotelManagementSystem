package hms;

public class PremiumSuite extends Room {
    public PremiumSuite(String roomID, String status) {
        super(roomID, status);
    }

    public String getName() { return "Premium Suite"; }
    public double getPriceRate() { return 99.99; }
}
