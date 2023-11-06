package kr.co.sugarmanager.userservice.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum UserInfoValidation {
    NAME(2, 20, false, null, "이름은 2~20자로 입력해주세요."),
    NICKNAME(6, 20, false, "^[가-힣a-zA-Z0-9!@#$%^&*()_+{}\\[\\]:;<>,.?/~\\\\-]{6,20}$", "닉네임은 한글, 영문, 숫자로 이루어진 6~20자 입니다."),
    HEIGHT(0, 250, true, null, "신장은 0.0 ~ 250.0 사이로 입력해주세요."),
    WEIGHT(0, 300, true, null, "체중은 0.0 ~ 300.0 사이로 입력해주세요."),
    GENDER(0, 100, true, "^(MALE|FEMALE)$", "성별은 MALE 혹은 FEMALE로 입력해주세요.");

    private int min, max;
    private String reg, message;
    private boolean nullable;

    UserInfoValidation(int min, int max, boolean nullable, String reg, String message) {
        this.min = min;
        this.max = max;
        this.nullable = nullable;
        this.reg = reg;
        this.message = message;
    }

    public boolean validate(String str) {
        if (str == null || str.trim().equals("")) return this.nullable;
        if (str.length() < this.min || this.max < str.length()) {
            return false;
        }
        if (this.reg == null) {
            return true;
        }

        Pattern pattern = Pattern.compile(this.reg);

        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }

    public boolean validate(Integer value) {
        return value == null ? this.nullable : this.min <= value && value <= this.max;
    }

    public String getMessage() {
        return this.message;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

}
