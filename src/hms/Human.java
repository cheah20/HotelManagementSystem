package hms;

public class Human {
    private final String name;
    private final String ic;
    private final String phone;
    private final boolean male;

    public Human(String name, String ic, String phone, boolean male) {
        this.name = name;
        this.ic = ic;
        this.phone = phone;
        this.male = male;
    }

    public String getName() { return name; }
    public String getIc() { return ic; }
    public String getPhone() { return phone; }
    public boolean isMale() { return male; }
}
