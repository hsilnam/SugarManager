package kr.co.sugarmanager.userservice.user.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public enum PokeType {
    CHALLENGE, BLOODSUGAR;

    @JsonCreator
    public static PokeType getPokeType(String value) {
        for (PokeType type : PokeType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return null;
    }

    public static String getDomain() {
        return Arrays.stream(PokeType.values())
                .map(PokeType::name)
                .collect(Collectors.joining(","));
    }
}
