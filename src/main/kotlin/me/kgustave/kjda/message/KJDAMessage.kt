/*
 * Copyright 2017 Kaidan Gustave
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("KJDAMessage")
@file:Suppress("UseExpressionBody", "Unused")
package me.kgustave.kjda.message

import me.kgustave.kjda.succeed
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.requests.RestAction

val argumentSplitter = Regex("\\s+")

infix operator fun Message.div(max: Int): List<String> {
    return rawContent.split(argumentSplitter, max)
}

operator fun Message.get(index: Int) = (this / 0)[index]

inline fun MessageChannel.send(builder: MessageBuilder,
                               block: MessageBuilder.() -> Unit): RestAction<Message> = sendMessage(builder.apply(block).build())
infix inline fun MessageChannel.send(block: MessageBuilder.() -> Unit): RestAction<Message> = send(MessageBuilder(), block)

infix inline fun MessageChannel.sendText(block: () -> String): RestAction<Message> = sendMessage(block())

inline fun Message.edit(builder: MessageBuilder,
                        block: MessageBuilder.() -> Unit): RestAction<Message> = editMessage(builder.apply(block).build())
infix inline fun Message.edit(block: MessageBuilder.() -> Unit): RestAction<Message> = edit(MessageBuilder(), block)

infix inline fun Message.editText(block: () -> String): RestAction<Message> = editMessage(block())

@Throws(Exception::class)
infix inline suspend fun MessageChannel.sendTextAsync(block: () -> String): Message = sendText(block).succeed()

@Throws(Exception::class)
infix inline suspend fun Message.editTextAsync(block: () -> String): Message = editText(block).succeed()

@Throws(Exception::class)
infix inline suspend fun Message.editAsync(block: MessageBuilder.() -> Unit): Message = edit(block).succeed()