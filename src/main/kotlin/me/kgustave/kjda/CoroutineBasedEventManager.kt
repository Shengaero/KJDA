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
package me.kgustave.kjda

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import kotlinx.coroutines.experimental.launch
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import net.dv8tion.jda.core.hooks.IEventManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Kaidan Gustave
 */
class CoroutineBasedEventManager : IEventManager {
    companion object {
        @JvmField val THREAD_NUMBER: AtomicInteger = AtomicInteger(0)
        private val logger: Logger = LoggerFactory.getLogger("EventManager")
        private val dispatcher: CoroutineDispatcher by lazy {
            newCachedThreadPool {
                Thread(it, "EventThread-${THREAD_NUMBER.getAndIncrement()}").also { it.isDaemon = true }
            }.asCoroutineDispatcher()
        }
    }

    private val listeners: MutableSet<EventListener> = CopyOnWriteArraySet()

    override fun handle(event: Event) {
        launch(dispatcher) {
            listeners.forEach {
                dispatcher.dispatch(coroutineContext, Runnable {
                    try {
                        it.onEvent(event)
                    } catch(e: Throwable) {
                        logger.error("An EventListener caught an exception:", e)
                    }
                })
            }
        }
    }

    override fun register(listener: Any) {
        listeners += requireNotNull(listener as? EventListener) {
            "Provided listener does not implement EventListener!"
        }
    }

    override fun getRegisteredListeners(): List<Any> = listeners.map { it as Any }

    override fun unregister(listener: Any) {
        if(listener is EventListener) {
            listeners -= listener
        }
    }
}