package com.backend.schedule.model.builder;

import com.backend.schedule.dto.ScheduleDto;
import com.backend.schedule.model.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleModelBuilder {
    enum frequency {
        Weekdays, Daily, Weekly, Monthly
    }

    public static void withScheduleDto(ScheduleDto scheduleDto, Schedule schedule) throws ParseException {
        String startDateString = scheduleDto.getStartDate().replaceAll(" ", "-");
        String endDateString = scheduleDto.getEndDate().replaceAll(" ", "-");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date startDate = formatter.parse(startDateString);
        Date endDate = formatter.parse(endDateString);
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setTime(scheduleDto.getTime());
        schedule.setDuration(scheduleDto.getDuration());
        schedule.setRepeat(scheduleDto.getRepeat());
        if(scheduleDto.getFrequency() != null)
            schedule.setFrequency(ScheduleModelBuilder.frequency.valueOf(scheduleDto.getFrequency()).ordinal());
    }
}
