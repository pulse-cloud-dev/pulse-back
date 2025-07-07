package pulse.back.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pulse.back.common.repository.MentoringViewLogRepository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class MentoringViewLogResetScheduled {

    private final MentoringViewLogRepository mentoringViewLogRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    public void resetMentoringViewLogsAtMidnight() {
        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        log.info("================= MentoringViewLogResetScheduled - start =================");

        mentoringViewLogRepository.deleteAllMentoringViewLogs()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(unused -> log.info("================= MentoringViewLogResetScheduled - success ================="))
                .doOnError(error -> log.error("================= MentoringViewLogResetScheduled - error =================", error))
                .subscribe();
    }
}