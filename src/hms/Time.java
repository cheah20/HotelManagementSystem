package hms;

import java.time.LocalTime;

public class Time {
    private final int hour;
    private final int minute;

    public Time(int hour, int minute) throws TimeException {
        if (hour < 0 || hour > 23) throw new TimeException("Invalid hour: " + hour);
        if (minute < 0 || minute > 59) throw new TimeException("Invalid minute: " + minute);
        this.hour = hour;
        this.minute = minute;
    }

    // reads "HH:MM"
    public static Time parse(String text) throws TimeException {
        String[] parts = text.trim().split(":");
        if (parts.length != 2) throw new TimeException("Time must be HH:MM");
        try {
            return new Time(Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()));
        } catch (NumberFormatException e) {
            throw new TimeException("Time must be HH:MM");
        }
    }

    public LocalTime toLocalTime() { return LocalTime.of(hour, minute); }

    public String toString() {
        return String.format("%02d:%02d", hour, minute);
    }
}
