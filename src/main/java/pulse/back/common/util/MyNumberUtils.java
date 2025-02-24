package pulse.back.common.util;

import org.springframework.stereotype.Component;

@Component
public class MyNumberUtils {

    public static int getTotalPages(Long totalCount, int size){
        return (totalCount ==null || totalCount<1) ? 1
                : (int) (totalCount % size == 0 ? (totalCount / size)
                : (totalCount / size) + 1);
    }

}