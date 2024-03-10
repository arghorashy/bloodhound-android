package com.mumbo.bloodhound;

import java.util.ArrayList;
import java.util.List;

public class BloodhoundConfigRow {
    public int id;
    public String buttonName;
    public String text;
    public String option1;
    public String option2;
    public String option3;
    public String scaleMax;

    public String toString() {
        List<String> strings = new ArrayList<>();
        strings.add(String.valueOf(id));
        strings.add(buttonName);
        strings.add(text);
        strings.add(option1);
        strings.add(option2);
        strings.add(option3);
        strings.add(scaleMax);
        return String.join(", ", strings);
    }
}
