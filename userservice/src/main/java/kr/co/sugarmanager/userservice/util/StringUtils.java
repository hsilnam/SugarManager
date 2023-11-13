package kr.co.sugarmanager.userservice.util;

import java.util.Random;

public class StringUtils {
    public static boolean isNull(String str) {
        return str == null;
    }

    public static boolean isBlank(String str) {
        return isNull(str) || str.trim().equals("");
    }

    public static String generateRandomString(int len){
        Random random = new Random(System.currentTimeMillis());
        int numberOffset = 48;
        int lowerOffset = 65;
        int upperOffset = 97;

        int[] offsets = {numberOffset, lowerOffset, upperOffset};
        int[] sizes = {10, 26, 26};

        StringBuffer sb = new StringBuffer();
        while (sb.length() < len) {
            int offsetIndex = random.nextInt(offsets.length);

            int sizeIndex = random.nextInt(sizes[offsetIndex]);

            sb.append((char) (offsets[offsetIndex] + sizeIndex));
        }

        return sb.toString();
    }
}
