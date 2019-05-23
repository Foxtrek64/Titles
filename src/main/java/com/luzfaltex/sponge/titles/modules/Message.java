package com.luzfaltex.sponge.titles.modules;

import com.luzfaltex.sponge.titles.Utilities.TextUtils;

public enum Message {

    Prefix("&7&l[&b&lTitles&7&l] ", false),

    ViewAvailableCommands("&3Use &a/{} help &3 to view available commands.", true),

    Log("{Prefix}&3LOG &3&l> &8(&e{}&8) [&a{}&8] (&b{}&8) \n" +
            "{Prefix}&3LOG &f{}",
    false),

    Info("&2Running &bTitles v{}&2 by &b LuzFaltex.", true),

    CommandNotFound("&cCommand not found.", true),
    LacksPermission("&cYou do not have permission to use this command!", true),

    MainCommandUsageHeader("&b{} Sub Commands: &7({} ...)", true),
    CommandUsageArgumentJoin("&3 - &7", false),
    CommandUsageBrief("&3> &a{}{}", false),
    CommandUsageDetailHeader(
            "{PREFIX}&3&lCommand Usage &3- &b{}" + "\n" +
                    "{PREFIX}&b> &7{}",
            false
    ),
    CommandUsageDtailedArgsHeader("&3Arguments:", true),
    CommandUsageDetailedArg("&b- {}&3 -> &7{}", true),
    RequiredArgument("&8<&7{}&8>", false),
    OptionalArgument("&8[&7{}&8]", false),

    /*
     * Loading / Saving
     */
    UserNotFound("&cA user for &4{}&c could not be found.", true),
    UserNotOnline("&aUser &b{}&a is not online.", true),
    UserSaveError("&cThere was an error whilst saving user data for &4{}&c.", true),

    SetTitleSuccess("&bYour title was set to &a{}", true),
    SetTitleFailure("{Prefix}&cAn error occurred when assigning your title!\n{Prefix}&c{}", false),
    GetTitleFailure("{Prefix}&cAn error occurred when assigning your title!\n{Prefix}&c{}", false),
    AddTitleSuccess("&bAdded &a{} &3 to &a{}", true),
    AddTitleFailure("{Prefix}&cAn error occurred when adding a title to &3{}!\n{Prefix}&c{}", false),
    RemoveTitleSuccess("&bRemoved &a{} &3 from &a{}", true),
    RemoveTitleFailure("&cAn error occurred when removing a title from &3{}!\n{Prefix}&c{}", false),

    SelectedTitleSelf("&bYour currently selected title is &a{}", true),
    SelectedTitleOther("&a{}'s &b selected title is currently &a{}", true),

    TitlesList("&aTitles:", true),
    TitlesListEntry("&f- &3{}", true),

    CommandsList("&aCommands:", true),
    CommandsListEntry("&f- &3{} &f({}) &b{}", true);


    private final String message;
    private final boolean showPrefix;

    Message(String message, boolean showPrefix) {
        this.message = TextUtils.rewritePlaceholders(message);
        this.showPrefix = showPrefix;
    }

    public String getRawMessage() {
        return this.message;
    }

    public String getFormattedMessage(Object... objects) {
        String s = showPrefix ? Prefix.message : "";
        s = format(message, objects);
        s = colorize(s);
        return s;
    }

    private static String format(String s, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            s = s.replace("{Prefix}", colorize(Prefix.getRawMessage()));
            s = s.replace("{" + i + "}", String.valueOf(o));
        }
        return s;
    }

    private static String colorize(String s) {
        char[] b = s.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}
