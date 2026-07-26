package likelion14th.lte.statistic.entity;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.todo.entity.WeekEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "statistic")
public class Statistic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int streak;

    @Column(nullable = false)
    private int monthPercent;

    @OneToMany(mappedBy = "statistic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StatWeek> statWeeks = new ArrayList<>();

    public static Statistic create() {
        Statistic statistic = new Statistic();
        statistic.streak = 0;
        statistic.monthPercent = 0;
        statistic.initializeWeeks();
        return statistic;
    }

    private void initializeWeeks() {
        for (WeekEnum week : WeekEnum.values()) {
            this.statWeeks.add(StatWeek.create(week, this));
        }
    }

    // streak 증가/초기화 로직 (도메인 로직은 엔티티에!)
    public void increaseStreakIfSuccess(boolean success) {
        if (success) {
            this.streak += 1;
        } else {
            this.streak = 0;
        }
    }

    public void updateMonthPercent(int percent) {
        this.monthPercent = percent;
    }

    // 가장 투두를 많이 완료한 요일 찾기
    public WeekEnum getMostTodoWeek() {
        return statWeeks.stream()
                .max(Comparator.comparingInt(StatWeek::getCount))
                .map(StatWeek::getWeek)
                .orElse(null);
    }
}