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
package me.kgustave

import kotlinx.coroutines.experimental.launch
import me.kgustave.kjda.*
import me.kgustave.kjda.message.editText
import me.kgustave.kjda.message.editTextAsync
import me.kgustave.kjda.message.sendText
import me.kgustave.kjda.message.sendTextAsync
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.script.ScriptEngineManager

fun main(args: Array<String>) {
    bot(async = true) {
        token { "BOT_TOKEN_HERE" }
        eventManager { CoroutineBasedEventManager() }
        on<MessageReceivedEvent> { onMessage(it) }
    }
}

fun onMessage(event: MessageReceivedEvent) = launch {
    val rawContent = event.message.rawContent
    when {
        rawContent.equals("!ping", true) or rawContent.equals("!pong", true) -> {
            // A simple Ping message
            val message = event.channel.sendMessage("Ping...").succeed()
            message.editTextAsync { "Ping: ${event.message.creationTime.until(message.creationTime, ChronoUnit.MILLIS)}ms" }
        }
        rawContent.equals("!wait", true) -> {
            // This sends the text "Waiting" to the channel and
            // then sends "I am done waiting!" 10 seconds later.
            event.channel
                .sendTextAsync { "Waiting" }
                .editText      { "I am done waiting!" }
                .succeedAfter(10, TimeUnit.SECONDS)
        }

        rawContent.startsWith("!eval ", true) -> {
            val se = ScriptEngineManager().getEngineByName("nashorn").apply {
                put("jda", event.jda)
                put("channel", event.channel)
                put("message", event.message)
            }

            val result = se.eval(rawContent.substring(5).trim())
            event.channel.sendText { result.toString() }.succeed()
        }

        rawContent.equals("!shutdown", true) -> {
            // Because sendTextAsync blocks the coroutine thread, this will execute flawlessly.
            event.channel.sendTextAsync { "Shutting down..." }

            event.jda.shutdown()
        }
    }
}