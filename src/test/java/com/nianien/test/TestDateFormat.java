package com.nianien.test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import com.nianien.core.date.DateFormatter;

public class TestDateFormat {

    @Test
    public void test() {
        System.out.println(DateFormatter.format(DateFormatter.getDate("2012-12-12 00:00:00")));
        SimpleDateFormat sdf = new SimpleDateFormat() {

            private static final long serialVersionUID = 1L;

            @Override
            public Date parse(String source) {
                String[] patterns = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "MM-dd HH:mm"};
                for (String pattern : patterns) {
                    try {
                        this.applyPattern(pattern);
                        return super.parse(source);
                    } catch (Exception ex) {
                    }
                }
                throw new IllegalArgumentException("date [" + source + "] must comply with the following format:" + Arrays.toString(patterns));
            }
        };
        System.out.println(sdf.toLocalizedPattern());
        sdf.applyPattern("yyyy-MM-dd");
        System.out.println(sdf.format(new Date()));

    }

}
