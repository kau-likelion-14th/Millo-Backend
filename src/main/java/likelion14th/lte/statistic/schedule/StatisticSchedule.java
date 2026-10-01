package likelion14th.lte.statistic.schedule;

import likelion14th.lte.statistic.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatisticSchedule {

    private final StatisticService statisticService;

    @Scheduled(cron = "0 10 0 * * *")
    public void updateAllStatisticsSchedule() {
        log.info("전체 유저 통계 갱신 스케줄러 시작");
        statisticService.updateAllStatistics();
        log.info("전체 유저 통계 갱신 스케줄러 완료");
    }
}