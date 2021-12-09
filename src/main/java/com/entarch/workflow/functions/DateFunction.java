package com.entarch.workflow.functions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateFunction {

    public static LocalDateTime fromDate(Date date) {
        if(date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
