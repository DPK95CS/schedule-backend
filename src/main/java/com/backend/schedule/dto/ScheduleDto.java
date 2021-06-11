package com.backend.schedule.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private String email;
    private String startDate;
    private String endDate;
    private String time;
    private String duration;
    private Boolean repeat;
    private String frequency;
}
