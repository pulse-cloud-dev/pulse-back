package pulse.back.common.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.domain.Sort;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PaginationDto<T>(
        Integer page,
        Integer size,
        Sort.Direction sort,
        Integer totalPages,
        Long totalCount,
        List<T> contents
) {

    public static class PaginationDtoBuilder<T> {
        private Integer page;
        private Integer size;
        private Sort.Direction sort;
        private Integer totalPages;
        private Long totalCount;
        private List<T> contents;

        public PaginationDtoBuilder<T> page(Integer page) {
            this.page = page;
            return this;
        }

        public PaginationDtoBuilder<T> size(Integer size) {
            this.size = size;
            return this;
        }

        public PaginationDtoBuilder<T> sort(Sort.Direction sort) {
            this.sort = sort;
            return this;
        }

        public PaginationDtoBuilder<T> totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PaginationDtoBuilder<T> totalCount(Long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public PaginationDtoBuilder<T> contents(List<T> contents) {
            this.contents = contents;
            return this;
        }

        public PaginationDto<T> build() {
            return new PaginationDto<>(page, size, sort, totalPages, totalCount, contents);
        }
    }

    // PaginationDtoBuilder 인스턴스를 생성하는 정적 메서드
    public static <T> PaginationDtoBuilder<T> builder() {
        return new PaginationDtoBuilder<>();
    }
}
