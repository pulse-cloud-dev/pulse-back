package pulse.back.common.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

@Slf4j
@Primary
@RequiredArgsConstructor
public class MentoRepositoryCustomImpl implements MentoRepositoryCustom {
    private final ReactiveMongoOperations mongoOperations;
}
