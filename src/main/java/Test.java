import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;



/**
 * @author David Esner
 */
public class Test {
	public static void main(String[] argv) {
    Instant dateTime = Instant.now();

    // UTC+9
    ZonedDateTime jpTime = dateTime.atZone(ZoneId.of("Asia/Tokyo"));
    TimeZone tz  = TimeZone.getTimeZone(ZoneId.of("Asia/Tokyo"));
    System.out.println(Instant.ofEpochMilli(dateTime.toEpochMilli()+tz.getOffset(dateTime.toEpochMilli())));
    System.out.println("ZonedDateTime : " + jpTime);

    // Convert to instant UTC+0/Z , java.time helps to reduce 9 hours
    Instant instant = jpTime.toInstant();

    System.out.println("Instant : " + instant);
	}
}

