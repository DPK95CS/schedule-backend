package com.backend.schedule.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "time")
    private String time;

    @Column(name = "duration")
    private String duration;

    @Column(name = "is_repeat")
    private Boolean repeat;

    @Column(name = "frequency")
    private Integer frequency;

}
