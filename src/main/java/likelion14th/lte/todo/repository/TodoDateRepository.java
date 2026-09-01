package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.entity.TodoDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoDateRepository extends JpaRepository<TodoDate, Long> {

    Optional<TodoDate> findByTodo_IdAndDate(Long todoId, LocalDate date);

    List<TodoDate> findAllByTodo_User_IdAndDate(Long userId, LocalDate date);

    List<TodoDate> findAllByTodo_IdAndDateBetween(Long todoId, LocalDate start, LocalDate end);

    void deleteAllByTodo_IdAndDateGreaterThanEqual(Long todoId, LocalDate from);

    List<TodoDate> findAllByTodo_User_IdAndDateBetween(
            Long userId, LocalDate start, LocalDate end
    );

    boolean existsByTodo_User_IdAndDateAndCompleted(Long userId, LocalDate date, boolean completed);

    int countByTodo_User_IdAndDateBetweenAndCompleted(Long userId, LocalDate start, LocalDate end, boolean completed);
}