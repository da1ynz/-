import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Seat {
    private int people;
    private LocalDateTime reservedTime;
    private LocalDateTime deadlineTime;

    public Seat(int people) {
        this.people = people;
        this.reservedTime = LocalDateTime.now();
        this.deadlineTime = reservedTime.plus(2, ChronoUnit.HOURS);
    }

    public int getPeople() {
        return people;
    }

    public String getReservedTime() {
        return reservedTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getDeadlineTime() {
        return deadlineTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public void extendTime(int hours) {
        if (deadlineTime != null) {
            deadlineTime = deadlineTime.plus(hours, ChronoUnit.HOURS);
        }
    }

    public void setReservedTime(LocalDateTime newTime) {
        this.reservedTime = newTime;
        this.deadlineTime = newTime.plus(2, ChronoUnit.HOURS);
    }

    public LocalDateTime getDeadlineTimeRaw() {
        return deadlineTime;
    }

    public void cancel() {
        this.people = 0;
        this.reservedTime = null;
        this.deadlineTime = null;
    }

    public boolean isReserved() {
        return reservedTime != null;
    }
}
