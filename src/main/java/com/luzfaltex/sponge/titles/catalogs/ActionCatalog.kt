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

package com.luzfaltex.sponge.titles.catalogs

import com.luzfaltex.sponge.titles.services.confirmation.IConfirmAction
import org.spongepowered.api.CatalogType
import org.spongepowered.api.command.CommandSource
import java.util.*
import kotlin.concurrent.schedule


class ActionCatalog(private val source: CommandSource, private val expirationSeconds: Long, private val action: IConfirmAction) : CatalogType {

    private val initTime: Long = System.currentTimeMillis()

    var isExpired: Boolean = false
    var completed: Boolean = false

    var timer = Timer()

    init {
        timer.schedule(expirationSeconds * 1000) {
            isExpired = true
            invokeDecline()
        }
    }

    /**
     * @returns commandSource.identifier
     */
    override fun getId(): String = source.identifier

    /**
     * @returns source.friendlyIdentifier or empty string
     */
    override fun getName(): String = source.friendlyIdentifier.orElse("")


    fun getSource(): CommandSource = source

    fun getAction(): IConfirmAction = action

    fun invokeAccept() {
        action.accept()
        completed = true
    }
    private fun invokeDecline() {
        action.decline()
        completed = true
    }


}