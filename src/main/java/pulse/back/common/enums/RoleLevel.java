package pulse.back.common.enums;

public enum RoleLevel {
    TEAM_MEMBER("팀원"),
    PART_LEADER("파트장"),
    TEAM_LEADER("팀장"),
    DIRECTOR("실장"),
    GROUP_LEADER("그룹장"),
    CENTER_HEAD("센터장"),
    MANAGER("매니저"),
    HEAD_OF_DIVISION("본부장"),
    BUSINESS_UNIT_HEAD("사업부장"),
    DIRECTOR_GENERAL("국장");

    private final String description;

    RoleLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

