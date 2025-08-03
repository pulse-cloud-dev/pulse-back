package pulse.back.common.enums;

public enum DocumentCategory {
    PROFILE_IMAGE("프로필 사진")
    ;



    private final String description;

    DocumentCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
