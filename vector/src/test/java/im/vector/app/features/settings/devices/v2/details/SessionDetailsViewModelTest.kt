/*
 * Copyright (c) 2022 New Vector Ltd
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

package im.vector.app.features.settings.devices.v2.details

import com.airbnb.mvrx.Success
import com.airbnb.mvrx.test.MvRxTestRule
import im.vector.app.features.settings.devices.v2.DeviceFullInfo
import im.vector.app.features.settings.devices.v2.overview.GetDeviceFullInfoUseCase
import im.vector.app.test.test
import im.vector.app.test.testDispatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo

private const val A_SESSION_ID = "session-id"

class SessionDetailsViewModelTest {

    @get:Rule
    val mvRxTestRule = MvRxTestRule(testDispatcher = testDispatcher)

    private val args = SessionDetailsArgs(
            deviceId = A_SESSION_ID
    )
    private val getDeviceFullInfoUseCase = mockk<GetDeviceFullInfoUseCase>()

    private fun createViewModel() = SessionDetailsViewModel(
            initialState = SessionDetailsViewState(args),
            getDeviceFullInfoUseCase = getDeviceFullInfoUseCase
    )

    @Test
    fun `given the viewModel has been initialized then viewState is updated with session info`() {
        // Given
        val deviceFullInfo = mockk<DeviceFullInfo>()
        val deviceInfo = mockk<DeviceInfo>()
        every { deviceFullInfo.deviceInfo } returns deviceInfo
        every { getDeviceFullInfoUseCase.execute(A_SESSION_ID) } returns flowOf(deviceFullInfo)
        val expectedState = SessionDetailsViewState(
                deviceId = A_SESSION_ID,
                deviceInfo = Success(deviceInfo)
        )

        // When
        val viewModel = createViewModel()

        // Then
        viewModel.test()
                .assertLatestState { state -> state == expectedState }
                .finish()
        verify { getDeviceFullInfoUseCase.execute(A_SESSION_ID) }
    }
}
