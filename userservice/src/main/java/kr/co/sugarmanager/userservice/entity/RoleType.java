package kr.co.sugarmanager.userservice.entity;

public enum RoleType {
    ADMIN("ROLE_ADMIN"), MEMBER("ROLE_MEMBER"), GUEST("ROLE_GUEST");

    private String value;

    RoleType(String value) {
        this.value = value;
    }
}
