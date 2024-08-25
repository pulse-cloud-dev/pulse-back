package pulse.back.common.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ResultData<T>(T body, String message) {
    public static class ResultDataBuilder<T> {
        private T body;
        private String message;

        public ResultDataBuilder<T> body(T body) {
            this.body = body;
            return this;
        }

        public ResultDataBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public ResultData<T> build() {
            return new ResultData<>(body, message);
        }
    }

    // ResultDataBuilder 인스턴스를 생성하는 정적 메서드
    public static <T> ResultDataBuilder<T> builder() {
        return new ResultDataBuilder<>();
    }
}