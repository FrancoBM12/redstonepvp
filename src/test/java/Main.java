import com.francobm.redstonepvp.cache.Items;
import com.francobm.redstonepvp.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        long daily = System.currentTimeMillis() + 86400000;
        long weekly = System.currentTimeMillis() + 604800000;
        long monthly = System.currentTimeMillis() + Long.parseLong("2592000000");
        Date now = new Date(System.currentTimeMillis());
        Date tmr = new Date(daily);
        Date week = new Date(weekly);
        String difference = Utils.friendlyTimeDiff(now, tmr);
        //System.out.println("Ahora: " + now + " Mañana: " + tmr + " Diferencia: " + difference);
        long millis = 3600 * 1000;
        //System.out.println("Ahora: " + now + " 5 Segundos: " + new Date(System.currentTimeMillis()+millis));
        //System.out.println("Ahora: " + friendlyTimeDiff(System.currentTimeMillis()) + " Mañana:" + friendlyTimeDiff(daily) + " Diferencia:" + friendlyTimeDiff((System.currentTimeMillis() - daily) /1000));
        List<String> strings = new ArrayList<>();
        strings.add("hola:a");
        strings.add("hola:b");
        strings.add("hola:c");
        String saveItem = String.join("|", strings);
        System.out.println(saveItem);
    }
}
