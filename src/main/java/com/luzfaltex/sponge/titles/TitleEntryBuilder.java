package com.luzfaltex.sponge.titles;

import java.util.Optional;

public class TitleEntryBuilder {

    private Optional<Integer> _id;
    private Optional<String> _titleGroup;
    private Optional<String> _title;

    public TitleEntryBuilder withId(int id) {
        _id = Optional.of(id);
        return this;
    }

    public TitleEntryBuilder withTitle(String title) {
        _title = Optional.of(title);
        return this;
    }

    public TitleEntryBuilder withGroup(String titleGroup) {
        _titleGroup = Optional.of(titleGroup);
        return this;
    }

    /**
     * Builds a TitleEntry object
     * @return A built TitleEntry
     * @throws IllegalStateException When not all properties have been specified.
     */
    public TitleEntry build() throws IllegalStateException {
        if (!_id.isPresent() || !_title.isPresent() || !_title.isPresent())
            throw new IllegalStateException();

        return new TitleEntry(_id.get(), _title.get(), _titleGroup.get());
    }

}
