package pulse.back.common.enums;

public enum EducationStatus {
    GRADUATED("졸업"),
    EXPECTED_GRADUATION("졸업예정"),
    ENROLLED("재학중"),
    DROPPED_OUT("중퇴"),
    ON_LEAVE("휴학");

    private final String description;

    EducationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
