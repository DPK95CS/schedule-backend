package com.backend.schedule.repository;

import com.backend.schedule.model.Schedule;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends CrudRepository<Schedule,Long> {
    List<Schedule> findByEmployeeId(Long employeeId);
    List<Schedule> findAll();
    List<Schedule> findByIdAndEmployeeId(Long id,Long employeeId);
    List<Schedule> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date startDate,Date endDate);
}
