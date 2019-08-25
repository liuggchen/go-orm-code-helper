package org.shonminh.helper.util;

public class StringUtil {

    public static boolean isSqlComment(String string) {
        return (string.trim().startsWith("--"));
    }

    public static String camelString(String string, boolean firstCharacterUpper) {
        char[] array = string.toCharArray();
        char[] retArray = new char[array.length];
        if (array.length < 2) {
            return string;
        }

        int j = 0;
        for (int i = 0; i < array.length; i++, j++) {
            if (i != 0 && array[i] == '_' && i + 1 < array.length && (Character.isUpperCase(array[i + 1]) || Character.isLowerCase(array[i + 1]))) {
                retArray[j] = Character.toUpperCase(array[i + 1]);
                i++;
            } else {
                retArray[j] = array[i];

                if (i == 0) {
                    if (Character.isLowerCase(retArray[j]) || Character.isUpperCase(retArray[j])) {
                        retArray[j] = Character.toUpperCase(retArray[j]);
                    }
                }
            }
        }

        return new String(retArray, 0, j);
    }
}
