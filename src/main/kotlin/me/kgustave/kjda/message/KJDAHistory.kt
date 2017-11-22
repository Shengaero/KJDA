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
@file:[JvmName("KJDAHistory") Suppress("Unused")]
package me.kgustave.kjda.message

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.channels.ProducerJob
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking
import me.kgustave.kjda.succeed
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageChannel
import net.dv8tion.jda.core.entities.MessageHistory
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.buildSequence

fun MessageHistory.paginated(context: CoroutineContext = DefaultDispatcher, limit: Int = 100): ProducerJob<Message> =
    produce(context) {
        try {
            // Return now
            if(limit <= 100)
                return@produce retrievePast(limit).succeed().forEach { send(it) }

            val queue: Queue<Message> = LinkedList(retrievePast(100).succeed())
            var lim = limit - 100

            do {
                while(queue.isNotEmpty())
                    send(queue.poll())
                queue.addAll(retrievePast(100).succeed())
                lim -= 100 // Increment down
            } while(queue.isNotEmpty() && lim > 100)

            if(lim > 0)
                retrievePast(lim).succeed().forEach { send(it) }
        } catch(e: Throwable) {
            // Close if there's an exception thrown.
            close(e)
        }
    }

fun MessageChannel.messages(context: CoroutineContext = DefaultDispatcher): ProducerJob<Message> = produce(context) {
    val iter = iterableHistory
    iter.forEach { send(it) }
    close()
}

fun MessageChannel.messageSequence(context: CoroutineContext): Sequence<Message> {
    val messages = messages(context)
    return buildSequence {
        runBlocking(context) { messages.receiveOrNull() }
    }
}