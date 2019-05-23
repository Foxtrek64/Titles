package com.luzfaltex.sponge.titles.Utilities;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TextUtils {

    private TextUtils() {}

    public static String joinNewLine(String... strings) {
        return joinNewLine(Arrays.stream(strings));
    }

    public static String joinNewLine(Stream<String> strings) {
        return strings.collect(Collectors.joining("\n"));
    }

    public static String rewritePlaceholders(String input) {
        int i = 0;
        while (input.contains("{}")) {
            input = input.replaceFirst("\\{\\}", "{" + i++ + "}");
        }
        return input;
    }
}
