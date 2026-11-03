package hms;

import java.time.LocalDate;

public class Date {
    private final int year;
    private final int month;
    private final int day;

    public Date(int year, int month, int day) throws DateException {
        if (year < 2000 || year > 2100) throw new DateException("Invalid year: " + year);
        if (month < 1 || month > 12) throw new DateException("Invalid month: " + month);
        // month length and leap year
        int last = LocalDate.of(year, month, 1).lengthOfMonth();
        if (day < 1 || day > last) throw new DateException("Invalid day: " + day + " (month " + month + " has " + last + " days)");
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public static Date today() {
        LocalDate d = LocalDate.now();
        try {
            return new Date(d.getYear(), d.getMonthValue(), d.getDayOfMonth());
        } catch (DateException e) {
            throw new IllegalStateException(e);
        }
    }

    public LocalDate toLocalDate() { return LocalDate.of(year, month, day); }

    // DD/MM/YYYY
    public String toString() {
        return String.format("%02d/%02d/%04d", day, month, year);
    }
}
