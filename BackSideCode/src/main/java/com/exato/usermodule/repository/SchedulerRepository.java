package com.exato.usermodule.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.exato.usermodule.entity.Scheduler;

@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler, Long> {

	// Fetch daily schedules for the given time range
	@Query("SELECT s FROM Scheduler s WHERE s.scheduleType = :scheduleType AND s.scheduleTime BETWEEN :startTime AND :endTime")
	List<Scheduler> findByScheduleTypeAndScheduleTimeBetween(@Param("scheduleType") String scheduleType,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	// Fetch weekly schedules for the given day and time range
	@Query("SELECT s FROM Scheduler s WHERE s.scheduleType = :scheduleType AND s.weeklyScheduleDay = :dayOfWeek AND s.scheduleTime BETWEEN :startTime AND :endTime")
	List<Scheduler> findByScheduleTypeAndWeeklyScheduleDayAndScheduleTimeBetween(
			@Param("scheduleType") String scheduleType, @Param("dayOfWeek") String dayOfWeek,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	// Fetch monthly schedules for the given day of the month and time range
	@Query("SELECT s FROM Scheduler s WHERE s.scheduleType = :scheduleType AND s.monthlyScheduleDate = :dayOfMonth AND s.scheduleTime BETWEEN :startTime AND :endTime")
	List<Scheduler> findByScheduleTypeAndMonthlyScheduleDateAndScheduleTimeBetween(
			@Param("scheduleType") String scheduleType, @Param("dayOfMonth") int dayOfMonth,
			@Param("startTime") Date startTime, @Param("endTime") Date endTime);

	List<Scheduler> findByClientId(Long clientId);
}
