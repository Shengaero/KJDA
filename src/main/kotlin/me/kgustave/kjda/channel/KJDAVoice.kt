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
@file:Suppress("Unused")
package me.kgustave.kjda.channel

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel

inline fun <reified T: VoiceChannel> T.join(closeIfOpen: Boolean = false) {
    guild.audioManager.run {
        if(isAttemptingToConnect)
            return@run
        if(closeIfOpen && isConnected)
            closeAudioConnection()
        openAudioConnection(this@join)
    }
}

inline val <reified T: VoiceChannel> T.isConnected: Boolean
    inline get() = members.contains(guild.selfMember)

inline val <reified T: Guild> T.connectedVoiceChannel: VoiceChannel?
    inline get() = voiceChannels.find { it.isConnected }

inline val <reified T: JDA> T.connectedVoiceChannels: List<VoiceChannel>
    inline get() = voiceChannels.filter { it.isConnected }