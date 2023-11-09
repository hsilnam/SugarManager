package kr.co.sugarmanager.userservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import kr.co.sugarmanager.userservice.exception.ValidationException;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    @JsonCreator
    public static AlertType getAlertType(String value) {
        for (AlertType type : AlertType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new ValidationException(ErrorCode.CATEGORY_NOT_VALID_EXCEPTION);
    }

    public static String getDomain() {
        return Arrays.stream(AlertType.values())
                .map(AlertType::name)
                .collect(Collectors.joining(","));
    }
}
