package kr.co.sugarmanager.business.bloodsugar.dto;

public enum SUGARBLOODSTATUS {
    SAFETY, WARNING, DANGER;

    public static SUGARBLOODSTATUS getSugarBloodStatus(BLOODSUGARCATEGORY category, int level) {
        switch (category) {
            case BEFORE -> {
                if (level >= 70 && level <= 130) return SUGARBLOODSTATUS.SAFETY;
                else if (level >= 63 && level < 143) return SUGARBLOODSTATUS.WARNING;
                else if (level >= 0 && level < 1000) return SUGARBLOODSTATUS.DANGER;
            }
            case AFTER -> {
                if (level >= 90 && level <= 180) return SUGARBLOODSTATUS.SAFETY;
                else if (level >= 81 && level < 198) return SUGARBLOODSTATUS.WARNING;
                else if (level >= 0 && level < 1000) return SUGARBLOODSTATUS.DANGER;
            }
        }
        return null;
    }
}
