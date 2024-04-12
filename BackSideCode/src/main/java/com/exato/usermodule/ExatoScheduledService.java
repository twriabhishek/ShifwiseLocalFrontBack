package com.exato.usermodule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.exato.usermodule.entity.Scheduler;
import com.exato.usermodule.entity.SystemConfig;
import com.exato.usermodule.repository.SchedulerRepository;
import com.exato.usermodule.repository.SystemConfigRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExatoScheduledService {

	@Autowired
	private SchedulerRepository schedulerRepository;

	@Autowired
	private SystemConfigRepository systemConfigRepository;

	@Scheduled(cron = "0 */2 * * * ?")
	public void executeScheduledTasks() {
		Date now = Calendar.getInstance().getTime();
		log.info("Executing scheduled tasks at: " + now);

		// Fetch and process daily schedules for the current date
		List<Scheduler> dailySchedulers = schedulerRepository.findByScheduleTypeAndScheduleTimeBetween("Daily", now,
				getNextTwoMinutes(now));
		log.info("Fetch and process daily schedules for the current date : "+dailySchedulers);
		
		processSchedulerList(dailySchedulers);

		// Fetch and process weekly schedules for the current date
		List<Scheduler> weeklySchedulers = schedulerRepository
				.findByScheduleTypeAndWeeklyScheduleDayAndScheduleTimeBetween("Weekly", getDayOfWeek(now), now,
						getNextTwoMinutes(now));
		log.info("Fetch and process weekly schedules for the current date : "+weeklySchedulers);
		processSchedulerList(weeklySchedulers);

		// Fetch and process monthly schedules for the current date
		List<Scheduler> monthlySchedulers = schedulerRepository
				.findByScheduleTypeAndMonthlyScheduleDateAndScheduleTimeBetween("Monthly", getDayOfMonth(now), now,
						getNextTwoMinutes(now));
		log.info("Fetch and process monthly schedules for the current date : "+monthlySchedulers);
		processSchedulerList(monthlySchedulers);
	}

	private Date getNextTwoMinutes(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.MINUTE, 2);
		return calendar.getTime();
	}

	private String getDayOfWeek(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
		return simpleDateFormat.format(date);
	}

	private int getDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	// Process the list of schedulers (you can customize this method based on your
	// requirements)
	private void processSchedulerList(List<Scheduler> schedulerList) {

		for (Scheduler scheduler : schedulerList) {
			// Get the namespaceId from the scheduler
			Long namespaceId = scheduler.getNamespaceId();

			// Generate a UUID
			String uuid = generateUUID();

			// Fetch data based on namespaceId from SystemConfig entity
			List<SystemConfig> systemConfigs = systemConfigRepository.findByTenantNamespace_NamespaceId(namespaceId);

			// Further operations with systemConfigs
			for (SystemConfig systemConfig : systemConfigs) {
				// Your logic with systemConfig
				String fromVendorTemplate = systemConfig.getFromVendorTemplate();
				String toVendorTemplate = systemConfig.getToVendorTemplate();
				System.out.println("credentials" + fromVendorTemplate + "" + toVendorTemplate);

				// Use the generated UUID in your logic
				callOtherService(uuid, fromVendorTemplate, toVendorTemplate);
			}
		}
	}

	private String generateUUID() {
		return UUID.randomUUID().toString();
	}

	private void callOtherService(String uuid, String fromVendorTemplate, String toVendorTemplate) {
		// Your logic to call another service with the generated UUID
		log.info("Calling another service with UUID: " + uuid);
	}
}
