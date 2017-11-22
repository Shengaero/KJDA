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
@file:JvmName("KJDABuilder")
@file:Suppress("HasPlatformType", "Unused")
package me.kgustave.kjda

import com.neovisionaries.ws.client.WebSocketFactory
import net.dv8tion.jda.core.*
import net.dv8tion.jda.core.audio.factory.IAudioSendFactory
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.IEventManager
import net.dv8tion.jda.core.requests.SessionReconnectQueue
import okhttp3.OkHttpClient

inline fun bot(async: Boolean = false, block: JDABuilder.() -> Unit) = JDABuilder(AccountType.BOT)
    .apply(block).run { if(async) buildAsync() else buildBlocking() }

infix inline fun JDABuilder.buildAsync(block: JDABuilder.() -> Unit): JDA {
    block()
    return buildAsync()
}

infix inline fun JDABuilder.buildBlocking(block: JDABuilder.() -> Unit): JDA {
    block()
    return buildBlocking()
}

infix inline fun JDABuilder.token(lazy: () -> String) = setToken(lazy())
infix inline fun JDABuilder.listener(lazy: () -> Any) = addEventListener(lazy())
infix inline fun JDABuilder.game(lazy: () -> Game) = setGame(lazy())
infix inline fun JDABuilder.status(lazy: () -> OnlineStatus) = setStatus(lazy())
infix inline fun JDABuilder.idle(lazy: () -> Boolean) = setIdle(lazy())
infix inline fun JDABuilder.eventManager(lazy: () -> IEventManager) = setEventManager(lazy())
infix inline fun JDABuilder.reconnectQueue(lazy: () -> SessionReconnectQueue) = setReconnectQueue(lazy())
infix inline fun JDABuilder.enableShutdownHook(lazy: () -> Boolean) = setEnableShutdownHook(lazy())
infix inline fun JDABuilder.maxReconnectDelay(lazy: () -> Int) = setMaxReconnectDelay(lazy())
infix inline fun JDABuilder.websocketFactory(lazy: () -> WebSocketFactory) = setWebsocketFactory(lazy())
infix inline fun JDABuilder.httpClientBuilder(lazy: () -> OkHttpClient.Builder) = setHttpClientBuilder(lazy())
infix inline fun JDABuilder.corePoolSize(lazy: () -> Int) = setCorePoolSize(lazy())
infix inline fun JDABuilder.audioFactory(lazy: () -> IAudioSendFactory) = setAudioSendFactory(lazy())
infix inline fun JDABuilder.audioEnabled(lazy: () -> Boolean) = setAudioEnabled(lazy())
infix inline fun JDABuilder.shardRateLimiter(lazy: () -> ShardedRateLimiter) = setShardedRateLimiter(lazy())
infix inline fun <reified E: Event> JDABuilder.on(crossinline lazy: (E) -> Unit) = listener {
    EventListener { if(it is E) lazy(it) }
}
inline fun JDABuilder.shards(max: Int, lazy: () -> IntProgression): JDABuilder {
    for(i in lazy())
        useSharding(i, max)
    return this
}