package likelion14th.lte.statistic.service;

import jakarta.persistence.EntityManager;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.statistic.dto.response.StatisticResponse;
import likelion14th.lte.statistic.entity.StatWeek;
import likelion14th.lte.statistic.entity.Statistic;
import likelion14th.lte.todo.repository.TodoDateRepository;
import likelion14th.lte.user.entity.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticService {

    private final UserRepository userRepository;
    private final TodoDateRepository todoDateRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public StatisticResponse getStatistic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        return StatisticResponse.from(user.getStatistic());
    }

    @Transactional
    public void updateStatistic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        updateStatisticInternal(user);
    }

    @Transactional
    public void updateAllStatistics() {
        int page = 0;
        int size = 500;
        Page<User> userPage;

        do {
            userPage = userRepository.findAll(PageRequest.of(page, size));

            for (User user : userPage.getContent()) {
                updateStatisticInternal(user);
            }

            entityManager.flush();
            entityManager.clear();

            page++;
        } while (userPage.hasNext());
    }

    private void updateStatisticInternal(User user) {
        Long userId = user.getId();
        Statistic statistic = user.getStatistic();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        boolean hasCompleted = todoDateRepository.existsByTodo_User_IdAndDateAndCompleted(userId, yesterday, true);
        boolean hasIncomplete = todoDateRepository.existsByTodo_User_IdAndDateAndCompleted(userId, yesterday, false);
        boolean success = hasCompleted && !hasIncomplete;

        statistic.increaseStreakIfSuccess(success);

        if (success) {
            StatWeek statWeek = statistic.getStatWeeks().stream()
                    .filter(w -> w.getWeek().toDayOfWeek() == yesterday.getDayOfWeek())
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR));
            statWeek.increaseCount();
        }

        LocalDate start = yesterday.minusDays(30);
        int completedCount = todoDateRepository.countByTodo_User_IdAndDateBetweenAndCompleted(userId, start, yesterday, true);
        int incompleteCount = todoDateRepository.countByTodo_User_IdAndDateBetweenAndCompleted(userId, start, yesterday, false);
        int total = completedCount + incompleteCount;

        int percent = (total == 0) ? 0 : (completedCount * 100) / total;
        statistic.updateMonthPercent(percent);
    }
}