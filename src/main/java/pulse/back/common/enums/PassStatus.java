package pulse.back.common.enums;

public enum PassStatus {
    WRITTEN_PASS("필기합격"),
    FINAL_PASS("최종합격");

    private final String description;

    PassStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
