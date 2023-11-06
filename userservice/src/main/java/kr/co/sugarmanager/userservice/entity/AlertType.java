package kr.co.sugarmanager.userservice.entity;

public enum AlertType {
    POKE("pokeAlert"), CHALLENGE("challengeAlert"),
    BLOOD("sugarAlert");

    private String member;

    AlertType(String member) {
        this.member = member;
    }

    public String getMember() {
        return member;
    }
}
