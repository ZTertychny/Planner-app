package planner.adapter.out.geodata;

import dto.FormalizedAddress;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import planner.adapter.out.geodata.dto.DaDataCleanDto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DaDataCleanMapper {
    private static final Pattern DADATA_TZ = Pattern.compile("^UTC([+-])(\\d{1,2})$");

    public FormalizedAddress map(Pair<DaDataCleanDto, String> data) {
        return new FormalizedAddress()
                .setFormalized_address(data.getFirst().getResult())
                .setGeo_lat(data.getFirst().getGeo_lat())
                .setGeo_lon(data.getFirst().getGeo_lon())
                .setRaw_address(data.getSecond())
                .setTz(getTimeZone(data.getFirst().getTimezone()));
    }

    private String getTimeZone(String tzRaw) {
        if (tzRaw == null || tzRaw.isBlank()) return "+00:00";
        String left = tzRaw.split("/", 2)[0].trim();                  // "UTC+5"
        Matcher m = DADATA_TZ.matcher(left);
        if (!m.matches()) return "+00:00";

        String sign = m.group(1);                                     // "+" или "-"
        int hh = Integer.parseInt(m.group(2));
        if (hh < 0) hh = 0;
        else if (hh > 14) hh = 14;                // здравый предел

        return String.format("%s%02d:00", "+".equals(sign) ? "+" : "-", hh);
    }
}
