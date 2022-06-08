package vn.ngs.nspace.recruiting.share.dto.utils;

import vn.ngs.nspace.lib.utils.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtils {

    public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String DB_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final Date minDate() throws Exception {
        String MIN_DATE = "1000-01-01T00:00:00+0700";
        return DateUtil.toDate(MIN_DATE,FORMAT);
    }

    public static final Date maxDate() throws Exception {
        String MAX_DATE = "5000-01-01T00:00:00+0700";
        return DateUtil.toDate(MAX_DATE,FORMAT);
    }

    public static Date toDate(String dateStr, String pattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

}
