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

package com.luzfaltex.sponge.titles.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.luzfaltex.sponge.titles.Message
import com.luzfaltex.sponge.titles.Titles
import com.luzfaltex.sponge.titles.catalogs.GroupCatalog
import com.luzfaltex.sponge.titles.catalogs.TitleCatalog
import com.luzfaltex.sponge.titles.results.Result
import com.luzfaltex.sponge.titles.services.confirmation.IConfirmAction
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import java.util.Optional

@CommandAlias("titles")
class Commands : BaseCommand() {

    @Dependency
    private lateinit var plugin: Titles

    @HelpCommand
    @Subcommand("?|help")
    @CommandPermission("luzfaltex.titles.base")
    fun onHelp(source: CommandSource, help: CommandHelp) {
        val version: Optional<String> = plugin.pluginContainer.version
        source.sendMessage(Text.of(Message.Info.getFormattedMessage(version.orElse("???"))))
        help.showHelp()
    }

    @Subcommand("info")
    @Description("Shows plugin information")
    @CommandPermission("luzfaltex.titles.base")
    fun onUnknownCommand(source: CommandSource) {
        val version: Optional<String> = plugin.pluginContainer.version
        source.sendMessage(Text.of(Message.Info.getFormattedMessage(version.orElse("???"))))
        source.sendMessage(Text.of(Message.ViewAvailableCommands.getFormattedMessage("titles")))
    }

    @Subcommand("list")
    @Description("Provides a list of titles available to you.")
    @CommandPermission("luzfaltex.titles.list.base")
    fun onList(player: Player, @Default("1") page: Int) {

        // Get a list of all titles
        val availableTitles = plugin.dataService.getAvailableTitles(player.uniqueId)

        val builder = PaginationList.builder()
        builder.title(Text.of(Message.TitlesList.getFormattedMessage()))
        builder.padding(Text.of(" "))
        builder.contents(availableTitles.values.map { title: TitleCatalog -> Text.of(Message.TitlesListEntry.getFormattedMessage(title.name)) })

        builder.sendTo(player)
    }

    @Subcommand("get")
    @Description("Allows for a user to retrieve their own title.")
    @CommandPermission("luzfaltex.titles.get.base")
    fun onGet(source: Player) {
        val title = plugin.dataService.getSelectedTitle(source.uniqueId)
        source.sendMessage(Text.of(Message.SelectedTitleSelf.getFormattedMessage(title.name)))
    }

    @Subcommand("get")
    @Description("Allows for a user to retrieve a user's title.")
    @CommandPermission("luzfaltex.titles.get.others.base")
    fun onGetOther(source: CommandSource, @Values("@players") target: Player) {
        val title = plugin.dataService.getSelectedTitle(target.uniqueId)
        source.sendMessage(Text.of(Message.SelectedTitleOther.getFormattedMessage(target.name, title.name)))
    }

    @Subcommand("set")
    @Description("Allows for a user to set their own title.")
    @CommandPermission("luzfaltex.titles.set.base")
    @CommandAlias("use")
    fun onSet(player: Player, @Conditions("entitled") @Values("@ownTitles") titleId: String) {
        val availableTitles = plugin.dataService.getAvailableTitles(player.uniqueId)

        val message = when (val result = plugin.dataService.setSelectedTitle(player.uniqueId, availableTitles[titleId]!!)) {
            is Result.FromSuccess -> Text.of(Message.SetTitleSuccess.getFormattedMessage(availableTitles[titleId]))
            is Result.FromFailure -> Text.of(Message.SetTitleFailure.getFormattedMessage(result.reason))
        }

        player.sendMessage(message)
    }

    @Subcommand("set")
    @Description("Allows for a user to set another's title")
    @CommandPermission("luzfaltex.titles.set.others.base")
    fun onSetOther(source: CommandSource, @Values("@players") target: Player, @Conditions("entitledOther") @Values("@theirTitles") titleId: String) {
        val availableTitles = plugin.dataService.getAvailableTitles(target.uniqueId)

        val message = when (val result = plugin.dataService.setSelectedTitle(target.uniqueId, availableTitles[titleId]!!)) {
            is Result.FromSuccess -> Text.of(Message.SetTitleOtherSuccess.getFormattedMessage(target.name, availableTitles[titleId]))
            is Result.FromFailure -> Text.of(Message.SetTitleOtherFailure.getFormattedMessage(target.name, result.reason))
        }

        source.sendMessage(message)
    }

    @Subcommand("listgroups")
    @Description("Allows user to retrieve a list of groups")
    @CommandPermission("luzfaltex.titles.groups.list.base")
    fun onListGroups(source: CommandSource) {

        val builder = PaginationList.builder()
        builder.title(Text.of(Message.GroupsList.getFormattedMessage()))
        builder.padding(Text.of(" "))
        builder.contents(plugin.dataService.getAllGroups().map { group -> Text.of(Message.GroupsListEntry.getFormattedMessage(group.name)) })

        builder.sendTo(source)
    }

    @Subcommand("confirm")
    @Description("Confirm a sensitive operation")
    @CommandPermission("luzfaltex.titles.base")
    fun onConfirm(source: CommandSource) {
        val operationOpt = plugin.actionCatalogModule.getById(source.identifier)

        if (!operationOpt.isPresent) {
            source.sendMessage(Text.of(Message.ConfirmNoAction.getFormattedMessage()))
            return
        }

        val operation = operationOpt.get()

        operation.invokeAccept()

        source.sendMessage(Text.of(Message.ConfirmSuccess.getFormattedMessage()))

        // Clean up
        plugin.actionCatalogModule.removeCatalog(operation.id)
    }

    @Subcommand("reload")
    @Description("Reloads the titles from disk")
    @CommandPermission("luzfaltex.titles.reload.base")
    fun onReload(source: CommandSource) {
        // TODO Not Implemented
    }

    @Subcommand("create")
    class createModule : BaseCommand() {

        @Dependency
        private lateinit var plugin: Titles

        // /title create title
        @Subcommand("title")
        @Description("Allows user to create a new title")
        @CommandPermission("luzfaltex.titles.create.title.base")
        fun onTitleCreate(source: CommandSource, @Conditions("uniqueTitle") titleName: String, @Values("@groups") @Conditions("groupExists") groupId: String) {
            var title = TitleCatalog(titleName.toLowerCase(), titleName)

            val message = when (val result = plugin.dataService.addTitle(title, groupId)) {
                is Result.FromSuccess -> Text.of(Message.AddTitleSuccess.getFormattedMessage(titleName.toLowerCase(), groupId))
                is Result.FromFailure -> Text.of(Message.AddTitleFailure.getFormattedMessage(result.reason))
            }

            source.sendMessage(message)
        }

        @Subcommand("group")
        @Description("Allows user to create a new group")
        @CommandPermission("luzfaltex.titles.create.group.base")
        fun onGroupCreate(source: CommandSource, @Conditions("uniqueGroup") groupName: String) {
            var group = GroupCatalog(groupName.toLowerCase(), groupName)

            val message = when (val result = plugin.dataService.addGroup(group)) {
                is Result.FromSuccess -> Text.of(Message.CreateGroupSuccess.getFormattedMessage(groupName))
                is Result.FromFailure -> Text.of(Message.CreateGroupFailure.getFormattedMessage(result.reason))
            }

            source.sendMessage(message)
        }
    }

    @Subcommand("delete")
    class deleteModule : BaseCommand() {

        @Dependency
        private lateinit var plugin: Titles

        @Subcommand("title")
        @Description("Allows user to delete a title")
        @CommandPermission("luzfaltex.titles.delete.title.base")
        fun onTitleDelete(source: CommandSource, @Conditions("titleExists") titleId: String) {
            val title = plugin.dataService.findTitle(titleId).get()
            val groupOpt = plugin.dataService.findGroup(titleId)

            promptUser(source, groupOpt,
                    acceptAction ={
                        plugin.dataService.removeTitle(title, it.orElse(null)?.id)
                        source.sendMessage(Text.of(Message.ConfirmSuccess.getFormattedMessage()))
                    },
                    declineAction = {
                        source.sendMessage(Text.of(Message.ConfirmAborted.getFormattedMessage()))
                    })
        }

        @Subcommand("group")
        @Description("Allows user to delete a group")
        @CommandPermission("luzfaltex.titles.delete.title.base")
        fun onGroupDelete(source: CommandSource, @Conditions("groupExists") groupId: String) {
            val groupOpt = plugin.dataService.findGroup(groupId)

            if (groupOpt.isPresent) {
                val group = groupOpt.get()

                promptUser(source, group,
                        acceptAction = {
                            val titles = group.titles

                            for (title in titles) {
                                plugin.dataService.removeTitle(title, group.id)
                            }

                            plugin.dataService.removeGroup(group.id)
                            source.sendMessage(Text.of(Message.ConfirmSuccess.getFormattedMessage()))
                        },
                        declineAction = {
                            source.sendMessage(Text.of(Message.ConfirmAborted.getFormattedMessage()))
                        },
                        additionalInfo = "Deleting a group will delete all titles contained within the group.")
            }
        }

        private fun <T> promptUser(source: CommandSource, data: T, acceptAction: (t: T) -> Unit, declineAction: () -> Unit, additionalInfo: String? = null) {

            class ConfirmationAction : IConfirmAction {
                override fun accept() = acceptAction(data)
                override fun decline() = declineAction()
            }

            val additionalInfo
                    = if (Optional.ofNullable(additionalInfo).isPresent)
                        Text.builder(additionalInfo!!).color(TextColors.AQUA).append(Text.NEW_LINE).build()
                    else Text.NEW_LINE

            val commandText = Text.builder("Please type ").color(TextColors.AQUA)
                    .append(Text.builder("/titles confirm").color(TextColors.GREEN).onClick(TextActions.runCommand("/titles confirm")).build())
                    .append(Text.builder(" to continue.").color(TextColors.AQUA).build())
                    .build()

            val message = Text.builder(Message.ConfirmPrompt.getFormattedMessage())
                    .append(additionalInfo)
                    .append(commandText)
                    .build()

            // Tell the user they need to confirm.
            source.sendMessage(message)

            plugin.confirmationService.addAction(source, ConfirmationAction())
        }
    }

    @Subcommand("group")
    @Description("Modify a group")
    @CommandPermission("luzfaltex.titles.group.edit.base")
    fun onEdit(source: CommandSource,
               @Conditions("groupExists") @Values("@groups") groupId: String,
               @Values("@action") action: Action,
               @Values("@type") type: SubjectType,
               @Conditions("subject") @Values("@subject") subject: String) {

        val group = plugin.groupCatalogModule.getById(groupId).get()

        if (type == SubjectType.USER) {
            val player = Sponge.getServer().getPlayer(subject)
            if (player.isPresent) {
                if (action == Action.ADD) {
                    group.addUser(player.get().uniqueId)
                } else {
                    group.removeUser(player.get().uniqueId)
                }
            } else throw IllegalArgumentException(subject)
        } else {
            val title = plugin.titleCatalogModule.getById(subject)
            if (title.isPresent) {
                if (action == Action.ADD) {
                    group.addTitle(title.get())
                } else {
                    group.removeTitle(title.get())
                }
            } else throw IllegalArgumentException(subject)
        }
    }
}