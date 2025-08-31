package com.example.ddingsroom.suggest_post.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SuggestPostCategory {
    REPORT(0, "미예약사용자 신고"),
    LOST_AND_FOUND(1, "분실물"),
    EQUIPMENT_DAMAGE(2, "기물파손"),
    FACILITY_BREAKDOWN(3, "시설고장"),
    NOISE_POLLUTION(4, "소음공해"),
    ETC(5, "기타");


    private final int value;
    private final String name;

    SuggestPostCategory(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, SuggestPostCategory> NAME_MAP =
            Arrays.stream(SuggestPostCategory.values()).collect(Collectors.toMap(SuggestPostCategory::getName, Function.identity()));

    private static final Map<Integer, SuggestPostCategory> VALUE_MAP =
            Arrays.stream(SuggestPostCategory.values()).collect(Collectors.toMap(SuggestPostCategory::getValue, Function.identity()));

    public static SuggestPostCategory fromName(String name) {
        return NAME_MAP.get(name);
    }

    public static SuggestPostCategory fromValue(int value) {
        return VALUE_MAP.get(value);
    }

    public static boolean isValidName(String name){
        return NAME_MAP.containsKey(name);
    }

    public static List<String> getAllNames() {
        return Arrays.stream(SuggestPostCategory.values()).map(SuggestPostCategory::getName).collect(Collectors.toList());
    }

}
