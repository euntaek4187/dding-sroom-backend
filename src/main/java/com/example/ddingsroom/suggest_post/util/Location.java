package com.example.ddingsroom.suggest_post.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Location {
    STUDY_ROOM_1(1, "스터디룸1"),
    STUDY_ROOM_2(2, "스터디룸2"),
    STUDY_ROOM_3(3, "스터디룸3"),
    STUDY_ROOM_4(4, "스터디룸4"),
    STUDY_ROOM_5(5, "스터디룸5");

    private final int value;
    private final String name;

    Location(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, Location> NAME_MAP =
            Arrays.stream(Location.values()).collect(Collectors.toMap(Location::getName, Function.identity()));

    private static final Map<Integer, Location> VALUE_MAP =
            Arrays.stream(Location.values()).collect(Collectors.toMap(Location::getValue, Function.identity()));

    public static Location fromName(String name) {
        return NAME_MAP.get(name);
    }

    public static Location fromValue(int value) {
        return VALUE_MAP.get(value);
    }

    public static boolean isValidName(String name) {
        return NAME_MAP.containsKey(name);
    }

    public static List<String> getAllNames() {
        return Arrays.stream(Location.values()).map(Location::getName).collect(Collectors.toList());
    }
}
