package pulse.back.domain.admin.terms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

@Primary
@Slf4j
@RequiredArgsConstructor
public class TermsRepositoryCustomImpl implements TermsRepositoryCustom{
    private final ReactiveMongoOperations mongoOperations;
}
