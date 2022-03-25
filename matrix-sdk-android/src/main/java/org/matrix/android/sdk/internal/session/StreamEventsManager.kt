/*
 * Copyright 2021 The Matrix.org Foundation C.I.C.
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

package org.matrix.android.sdk.internal.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.LiveEventListener
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.internal.crypto.MXEventDecryptionResult
import timber.log.Timber
import javax.inject.Inject

@SessionScope
internal class StreamEventsManager @Inject constructor() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val listeners = mutableListOf<LiveEventListener>()

    fun addLiveEventListener(listener: LiveEventListener) {
        listeners.add(listener)
    }

    fun removeLiveEventListener(listener: LiveEventListener) {
        listeners.remove(listener)
    }

    fun dispatchLiveEventDecrypted(event: Event) {
        Timber.v("## dispatchLiveEventDecrypted ${event.eventId}")
        coroutineScope.launch {
            listeners.forEach {
                tryOrNull {
                    it.onEventDecrypted(event)
                }
            }
        }
    }

    fun dispatchLiveEventDecryptionFailed(event: Event, error: Throwable) {
        Timber.v("## dispatchLiveEventDecryptionFailed ${event.eventId}")
        coroutineScope.launch {
            listeners.forEach {
                tryOrNull {
                    it.onEventDecryptionError(event, error)
                }
            }
        }
    }

    fun dispatchOnLiveToDevice(event: Event) {
        Timber.v("## dispatchOnLiveToDevice ${event.eventId}")
        coroutineScope.launch {
            listeners.forEach {
                tryOrNull {
                    it.onLiveToDeviceEvent(event)
                }
            }
        }
    }
}
