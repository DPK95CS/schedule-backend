package com.backend.schedule.controller;

import com.backend.schedule.dto.ScheduleDto;
import com.backend.schedule.model.Employee;
import com.backend.schedule.model.Schedule;
import com.backend.schedule.model.builder.ScheduleModelBuilder;
import com.backend.schedule.repository.EmployeeRepository;
import com.backend.schedule.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ScheduleController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    private Date stringToDate(String dateInString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return formatter.parse(dateInString);
    }

    private boolean isBetween(Date date, Date startDate, Date endDate) {
        if (date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0) {
            return true;
        } else
            return false;
    }

    private Employee getEmployeeIdFromEmail(String email) {
        List<Employee> employeeList = employeeRepository.findByEmail(email);
        if (CollectionUtils.isEmpty(employeeList))
            return null;
        else
            return employeeList.get(0);
    }

    @PostMapping("/schedule")
    public String addSchedule(@RequestBody ScheduleDto scheduleDto) throws ParseException {
        String email = scheduleDto.getEmail();
        if (email == null || email.isEmpty())
            return "Please provide email data";
        Employee employee = getEmployeeIdFromEmail(email);

        if (employee == null) //new employee
        {
            employee = new Employee();
            employee.setEmail(email);
            employee = employeeRepository.save(employee);
        }
        Schedule schedule = new Schedule();
        schedule.setEmployeeId(employee.getId());
        ScheduleModelBuilder.withScheduleDto(scheduleDto, schedule);
        scheduleRepository.save(schedule);

        return "Schedule saved successfully for " + scheduleDto.getEmail();
    }

    @GetMapping("/getSchedule")
    public List<Schedule> getSchedule(@RequestParam("email") String email) {
        List<Schedule> scheduleList = new ArrayList<>();
        Employee employee = getEmployeeIdFromEmail(email);
        if (employee == null)
            return scheduleList;
        Long employeeId = employee.getId();
        scheduleList = scheduleRepository.findByEmployeeId(employeeId);
        return scheduleList;
    }

    @PostMapping("/modifySchedule/{id}")
    public String modifySchedule(@PathVariable(name = "id") String scheduleIdString, @RequestBody ScheduleDto scheduleDto) throws ParseException {
        Employee employee = getEmployeeIdFromEmail(scheduleDto.getEmail());
        if (employee == null)
            return "employeeId is invalid";
        Long employeeId = employee.getId();
        List<Schedule> scheduleData;
        Schedule schedule;
        Long scheduleId = Long.parseLong(scheduleIdString);
        scheduleData = scheduleRepository.findByIdAndEmployeeId(scheduleId, employeeId);
        if (!CollectionUtils.isEmpty(scheduleData))
            schedule = scheduleData.get(0);
        else
            return "scheduleId is invalid";

        ScheduleModelBuilder.withScheduleDto(scheduleDto, schedule);
        scheduleRepository.save(schedule);

        return "scheduleId successfully modified for employee: " + scheduleDto.getEmail();
    }

    @DeleteMapping("/cancelSchedule/{id}")
    public String cancelSchedule(@PathVariable(name = "id") String scheduleIdString, @RequestParam(name = "email") String email) {
        Employee employee = getEmployeeIdFromEmail(email);
        if (employee == null)
            return "employeeId is invalid";
        Long employeeId = employee.getId();

        List<Schedule> scheduleList;
        Long scheduleId = Long.parseLong(scheduleIdString);
        scheduleList = scheduleRepository.findByIdAndEmployeeId(scheduleId, employeeId);
        if (CollectionUtils.isEmpty(scheduleList))
            return "scheduleId is invalid";

        scheduleRepository.deleteById(scheduleId);
        return "scheduleId successfully cancelled for employee: " + email;
    }

    @GetMapping("/getScheduleByDate")
    public List<Schedule> getScheduleByDate(@RequestParam("Date") String input) throws ParseException {

        List<Schedule> result = new ArrayList<>();

        String dateInString = input.replaceAll(" ", "-");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = formatter.parse(dateInString);

        List<Schedule> scheduleList;
        scheduleList = scheduleRepository.findAllByStartDateLessThanEqualAndEndDateGreaterThanEqual(date, date);

        if (!CollectionUtils.isEmpty(scheduleList)) {
            for (Schedule schedule : scheduleList) {
                Date startDate = schedule.getStartDate();
                Date endDate = schedule.getEndDate();
                Boolean isRepeat = schedule.getRepeat();
                int frequency = -1;
                if (schedule.getFrequency() != null)
                    frequency = schedule.getFrequency();

                if ((!isRepeat) && isBetween(date, startDate, endDate)) //daily
                {
                    result.add(schedule);
                } else if (isRepeat) {
                    if (frequency == 0) //Weekdays
                    {
                        Calendar c = Calendar.getInstance();
                        c.setTime(date);
                        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                        if (dayOfWeek != 1 && dayOfWeek != 7)
                            result.add(schedule);
                    } else if (frequency == 1) //Daily
                    {
                        result.add(schedule);
                    } else if (frequency == 2)  //Weekly
                    {
                        Calendar c = Calendar.getInstance();
                        String workingDayInString;
                        Date workingDay;

                        for (Date start = startDate; ; ) {
                            if (start.equals(date)) {
                                result.add(schedule);
                                break;
                            }
                            c.setTime(start);
                            c.add(Calendar.DATE, 7);
                            workingDayInString = formatter.format(c.getTime());
                            workingDay = stringToDate(workingDayInString);
                            if (workingDay.after(endDate))
                                break;
                            start = workingDay;
                        }
                    } else if (frequency == 3) //Monthly
                    {
                        Calendar c = Calendar.getInstance();
                        String workingDayInString;
                        Date workingDay;

                        for (Date start = startDate; ; ) {
                            if (start.equals(date)) {
                                result.add(schedule);
                                break;
                            }
                            c.setTime(start);
                            c.add(Calendar.MONTH, 1);
                            workingDayInString = formatter.format(c.getTime());
                            workingDay = stringToDate(workingDayInString);
                            if (workingDay.after(endDate))
                                break;
                            start = workingDay;
                        }
                    }
                }
            }
        }
        return result;
    }
}