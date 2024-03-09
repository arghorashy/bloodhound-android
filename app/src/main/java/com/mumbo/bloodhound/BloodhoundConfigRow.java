package com.mumbo.bloodhound;

import java.util.ArrayList;
import java.util.List;

public class BloodhoundConfigRow {
    private int id;
    private String buttonName;
    private String text;
    private String option1;
    private String option2;
    private String option3;
    private String scaleMax;

    public String toString() {
        List<String> strings = new ArrayList<>();
        strings.add(String.valueOf(id));
        strings.add(buttonName);
        strings.add(text);
        strings.add(option1);
        strings.add(option2);
        strings.add(option3);
        return String.join(", ", strings);
    }
}
