package pulse.back.common.enums;

public enum EducationLevel {
    UNDERGRADUATE_2("대학교(2,3학년)"), // 2,3학년 대학생
    UNDERGRADUATE_4("대학교(4학년)"),   // 4학년 대학생
    MASTER("대학원(석사)"),            // 석사과정
    DOCTORATE("대학원(박사)");         // 박사과정

    private final String description;

    EducationLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}