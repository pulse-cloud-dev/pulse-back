package pulse.back.common;

import com.querydsl.core.types.Path;
import org.springframework.stereotype.Component;

@Component
public class QueryPathResolver {
    public static String get(Path<?> path) {
        return path.getMetadata().getName();
    }
}
