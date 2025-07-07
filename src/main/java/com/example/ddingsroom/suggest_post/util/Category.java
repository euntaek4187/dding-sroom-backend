package com.example.ddingsroom.suggest_post.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Category {
    LOST_AND_FOUND(1, "분실물"),
    EQUIPMENT_DAMAGE(2, "기물파손"),
    FACILITY_BREAKDOWN(3, "시설고장"),
    NOISE_POLLUTION(4, "소음공해"),
    ETC(5, "기타");


    private final int value;
    private final String name;

    Category(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, Category> NAME_MAP =
            Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getName, Function.identity()));

    private static final Map<Integer, Category> VALUE_MAP =
            Arrays.stream(Category.values()).collect(Collectors.toMap(Category::getValue, Function.identity()));

    public static Category fromName(String name) {
        return NAME_MAP.get(name);
    }

    public static Category fromValue(int value) {
        return VALUE_MAP.get(value);
    }

    public static boolean isValidName(String name){
        return NAME_MAP.containsKey(name);
    }

    public static List<String> getAllNames() {
        return Arrays.stream(Category.values()).map(Category::getName).collect(Collectors.toList());
    }

}
