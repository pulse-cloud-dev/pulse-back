package pulse.back.domain.mentoring.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

@Primary
@Slf4j
@RequiredArgsConstructor
public class MentoringRepositoryCustomImpl implements MentoringRepositoryCustom{
    private final ReactiveMongoOperations mongoOperations;
}
