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
@file:[Suppress("Unused", "HasPlatformType") JvmName("KJDAAudit")]
package me.kgustave.kjda.guild

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.channels.ProducerJob
import kotlinx.coroutines.experimental.channels.produce
import me.kgustave.kjda.succeed
import net.dv8tion.jda.core.audit.ActionType
import net.dv8tion.jda.core.audit.AuditLogEntry
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.requests.restaction.pagination.AuditLogPaginationAction
import kotlin.coroutines.experimental.CoroutineContext

infix inline fun AuditLogPaginationAction.limit(lazy: () -> Int) = limit(lazy())
infix inline fun AuditLogPaginationAction.type(lazy: () -> ActionType) = type(lazy())
infix inline fun AuditLogPaginationAction.user(lazy: () -> User) = user(lazy())

fun AuditLogPaginationAction.paginated(context: CoroutineContext = DefaultDispatcher): ProducerJob<AuditLogEntry> =
    produce(context) {
        try {
            succeed().forEach { send(it) }
            close()
        } catch(e: Throwable) {
            close(e)
        }
    }