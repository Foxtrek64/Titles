/*
 * MIT License
 *
 * Copyright (c) 2019 LuzFaltex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.luzfaltex.sponge.titles;

import com.luzfaltex.sponge.titles.utilities.TextUtils;

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
    CommandUsageDetailedArgsHeader("&3Arguments:", true),
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
    SetTitleOtherSuccess("&a{}'s &btitle was set to &a{}", true),
    SetTitleOtherFailure("{Prefix}&cAn error occurred when assigning &4{}'s &ctitle!\n{Prefix}&c{}", false),
    GetTitleFailure("{Prefix}&cAn error occurred when retrieving your title!\n{Prefix}&c{}", false),
    AddTitleSuccess("&bAdded &a{} &3to &a{}", true),
    AddTitleFailure("{Prefix}&cAn error occurred when adding a title to &3{}!\n{Prefix}&c{}", false),
    RemoveTitleSuccess("&bRemoved &a{} &3from &a{}", true),
    RemoveTitleFailure("&cAn error occurred when removing a title from &3{}!\n{Prefix}&c{}", false),

    SelectedTitleSelf("&bYour currently selected title is &a{}", true),
    SelectedTitleOther("&a{}'s &b selected title is currently &a{}", true),

    CreateTitleSuccess("&bCreated title &a{} &bin group &a{}", true),
    CreateTitleFailure("{Prefix}&cAn error occurred when creating the title!\n{Prefix}&c{}", false),
    DeleteTitleSuccess("&bSuccessfully deleted title &a{}", true),
    DeleteTitleFailure("{Prefix}&cAn error occurred when deleting the title!\n{Prefix}&c{}", false),

    CreateGroupSuccess("&bCreated group &a{}", true),
    CreateGroupFailure("{Prefix}&cAn error occurred when creating the group!\n{Prefix}&c{}", false),
    DeleteGroupSuccess("&bSuccessfully deleted group &a{}", true),
    DeleteGroupFailure("{Prefix}&cAn error occurred when deleting the group!\n{Prefix}&c{}", false),

    TitlesList("&aTitles", false),
    // id title
    TitlesListEntry("&f- &3{}", true),

    GroupsList("&aGroups", false),
    // group
    GroupsListEntry("&f- &3{}", true),

    CommandsList("&aCommands:", true),
    // command (aliases) description
    CommandsListEntry("&f- &3{} &f({}) &b{}", true),

    TitleNotFound("&cCould not find a title with the name &3{}", true),

    MissingArgument("&cYou failed to provide a valid argument for &3{}", true),

    ConfirmPrompt("$bThe requested operation requires confirmation to proceed.", true),
    ConfirmNoAction("&cNo pending confirmation actions", true),
    ConfirmSuccess("&bThe requested operation has completed successfully.", true),
    ConfirmExpired("&cThis confirmation action has expired. Please attempt the initial operation again.", true),
    ConfirmAborted("&bThis operation was aborted. No changes were made.", true);


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
        s = s + format(message, objects);
        s = colorize(s);
        return s;
    }

    private static String format(String s, Object... objects) {
        s = s.replace("{Prefix}", colorize(Prefix.getRawMessage()));
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
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
