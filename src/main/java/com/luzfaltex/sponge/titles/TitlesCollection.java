package com.luzfaltex.sponge.titles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitlesCollection {
    public final List<String> Titles;

    public TitlesCollection() {
        Titles = new ArrayList<>();
    }

    public TitlesCollection(List<String> titles) {
        Titles = titles;
    }

    public TitlesCollection(String... titles) {
        Titles = Arrays.asList(titles);
    }

    public TitlesCollection merge(TitlesCollection other) {
        for (String title : other.Titles) {
            if (Titles.contains(title)) continue;

            Titles.add(title);
        }

        return this;
    }
}
