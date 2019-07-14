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

import com.luzfaltex.sponge.titles.catalogs.ActionCatalog
import org.spongepowered.api.command.CommandSource

interface IConfirmationService {

    /**
     * Adds an action to the action list
     * @param source user
     * @param action action
     */
    fun addAction(source: CommandSource, action: IConfirmAction, duration: Long = 10)
    /**
     * Removes an action from the action list
     * @param source user
     */
    fun removeAction(source: CommandSource)

    /**
     * Retrieve an action via a CommandSender instance.
     *
     * @param source the commandSender
     * @return an instance of
     * @see IConfirmAction
     * Used to decline or confirm a command request
     * @see IConfirmAction#accept()
     * @see IConfirmAction#decline()
     */
    fun getAction(source: CommandSource) : ActionCatalog?

}