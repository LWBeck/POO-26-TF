import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;


public class App {
    public static void main(String[] args) {
        
        LocalDateTime a = LocalDateTime.of(2026, 6, 12, 12, 00);
        LocalDateTime b = LocalDateTime.of(2026, 6, 12, 17, 30);

        System.out.println(diffDias(a,b));
        System.out.println(diffHoras(a, b));

    }

    public static long diffHoras(LocalDateTime a, LocalDateTime b){
        return ChronoUnit.HOURS.between(a, b);
    }

    public static long diffDias(LocalDateTime a, LocalDateTime b){
        return b.getLong(ChronoField.EPOCH_DAY) - a.getLong(ChronoField.EPOCH_DAY);
    }
    
}
