package com.example.studentplacementmanagement.scheduler;

import com.example.studentplacementmanagement.entity.JobDrive;
import com.example.studentplacementmanagement.enums.DriveStatus;
import com.example.studentplacementmanagement.repository.JobDriveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDriveScheduler {

    private final JobDriveRepository jobDriveRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeExpiredJobDrives() {

        log.info("Starting scheduled job drive expiry check");

        LocalDateTime currentTime = LocalDateTime.now();

        List<JobDrive> expiredDrives =
                jobDriveRepository.findExpiredDrives(
                        DriveStatus.OPEN,
                        currentTime
                );

        if (expiredDrives.isEmpty()) {
            log.info("No expired job drives found");
            return;
        }

        for (JobDrive jobDrive : expiredDrives) {

            jobDrive.setStatus(DriveStatus.CLOSED);

            log.info(
                    "Job drive ID {} closed because application deadline expired",
                    jobDrive.getId()
            );
        }

        jobDriveRepository.saveAll(expiredDrives);

        log.info(
                "Scheduled job drive expiry completed. {} drives closed",
                expiredDrives.size()
        );
    }
}