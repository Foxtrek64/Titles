package com.luzfaltex.sponge.titles;

public class TitleEntry {
    /**
     * The unique Id for this title
     */
    public final int Id;

    /**
     * The text of the title
     */
    public final String Title;

    /**
     * The group the title belongs to.
     */
    public final String TitleGroup;

    /**
     * Gets the permission node for this object (luzfaltex.titles.group.{@literal TitleGroup}
     */
    public final String PermissionNode;

    public static final TitleEntryBuilder builder() {
        return new TitleEntryBuilder();
    }

    public TitleEntry(int id, String title, String titleGroup) {
        Id = id;
        Title = title;
        TitleGroup = titleGroup;
        PermissionNode = "luzfaltex.titles.group." + titleGroup;
    }
}
