package likelion14th.lte.todo.repository;

import likelion14th.lte.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    //스케쥴러에서 루틴인 애들을 전부 긁어다가 투두데이트 애들 생성하기위해
    List<Todo> findAllByRoutineEnabledTrue();
}
