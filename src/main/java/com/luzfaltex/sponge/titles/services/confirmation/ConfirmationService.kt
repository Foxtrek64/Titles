/*
 * MIT License
 *
 * Copyright (c) 2019 Glare
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
// Modified by Foxtrek_64 for LuzFaltex

package com.luzfaltex.sponge.titles.services.confirmation

import com.luzfaltex.sponge.titles.Titles
import com.luzfaltex.sponge.titles.catalogs.ActionCatalog
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.channel.MessageReceiver
import java.util.*

class ConfirmationService(private val plugin: Titles) : IConfirmationService {

    private val actions = HashMap<CommandSource, ActionCatalog>()

    override fun addAction(source: CommandSource, action: IConfirmAction, duration: Long) {
        val actionCatalog = ActionCatalog(source, duration, action)

        actions[source] = actionCatalog
        plugin.actionCatalogModule.registerAdditionalCatalog(actionCatalog)
    }

    override fun removeAction(source: CommandSource) {
        plugin.actionCatalogModule.removeCatalog(source.identifier)
        actions.remove(source)
    }

    override fun getAction(source: CommandSource): ActionCatalog? = actions[source]
}
