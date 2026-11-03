package hms;

public class Customer extends Human {
    public static final int MAX_LEVEL = 5;

    private final String customerID;
    private final int level;

    public Customer(String customerID, String name, String ic, String phone, boolean male, int level) {
        super(name, ic, phone, male);
        this.customerID = customerID;
        this.level = level;
    }

    public String getID() { return customerID; }
    public int getLevel() { return level; }

    // level 1 to 5
    private static final double[] DISCOUNT = { 0.00, 0.05, 0.10, 0.15, 0.20 };

    public double getDiscountRate() {
        return DISCOUNT[Math.min(Math.max(level, 1), MAX_LEVEL) - 1];
    }
}
