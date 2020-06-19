/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.camera.camera2.pipe

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.os.Build
import androidx.camera.camera2.pipe.testing.CameraPipeRobolectricTestRunner
import androidx.camera.camera2.pipe.testing.FakeCameraMetadata
import androidx.camera.camera2.pipe.testing.FakeMetadata
import androidx.camera.camera2.pipe.testing.FakeRequestMetadata
import androidx.camera.camera2.pipe.testing.FakeResultMetadata
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.robolectric.annotation.Config

@SmallTest
@RunWith(JUnit4::class)
class MetadataTest {
    @Test
    fun testMetadataCanRetrieveValues() {
        val metadata = FakeMetadata(mapOf(FakeMetadata.TEST_KEY to 42))

        assertThat(metadata[FakeMetadata.TEST_KEY]).isNotNull()
        assertThat(metadata[FakeMetadata.TEST_KEY_ABSENT]).isNull()

        assertThat(metadata.getOrDefault(FakeMetadata.TEST_KEY, 84))
            .isEqualTo(42)
        assertThat(metadata.getOrDefault(FakeMetadata.TEST_KEY_ABSENT, 84))
            .isEqualTo(84)
    }
}

@SmallTest
@RunWith(CameraPipeRobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP)
class CameraMetadataTest {

    @Test
    fun canRetrieveCameraCharacteristicsOrCameraMetadataViaInterface() {
        val metadata = FakeCameraMetadata(
            mapOf(CameraCharacteristics.LENS_FACING to CameraCharacteristics.LENS_FACING_FRONT),
            mapOf(FakeMetadata.TEST_KEY to 42)
        )

        assertThat(metadata[FakeMetadata.TEST_KEY]).isNotNull()
        assertThat(metadata[FakeMetadata.TEST_KEY_ABSENT]).isNull()

        assertThat(metadata[CameraCharacteristics.LENS_FACING]).isNotNull()
        assertThat(metadata[CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES]).isNull()
    }
}

@SmallTest
@RunWith(CameraPipeRobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP)
class RequestMetadataTest {

    @Test
    fun canRetrieveCaptureRequestOrCameraMetadataViaInterface() {
        val requestMetadata = FakeRequestMetadata(
            requestParameters = mapOf(CaptureRequest.JPEG_QUALITY to 95),
            request = Request(
                streams = listOf(),
                requestParameters = mapOf(CaptureRequest.JPEG_QUALITY to 20),
                extraRequestParameters = mapOf(FakeMetadata.TEST_KEY to 42)
            )
        )

        assertThat(requestMetadata[CaptureRequest.JPEG_QUALITY]).isEqualTo(95)
        assertThat(requestMetadata[CaptureRequest.COLOR_CORRECTION_MODE]).isNull()
        assertThat(requestMetadata[FakeMetadata.TEST_KEY]).isEqualTo(42)
        assertThat(requestMetadata[FakeMetadata.TEST_KEY_ABSENT]).isNull()

        assertThat(requestMetadata.request[CaptureRequest.JPEG_QUALITY]).isEqualTo(20)
        assertThat(requestMetadata.request[CaptureRequest.COLOR_CORRECTION_MODE]).isNull()
        assertThat(requestMetadata.request[FakeMetadata.TEST_KEY]).isEqualTo(42)
        assertThat(requestMetadata.request[FakeMetadata.TEST_KEY_ABSENT]).isNull()
    }
}

@SmallTest
@RunWith(CameraPipeRobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.LOLLIPOP)
class ResultMetadataTest {

    @Test
    fun canRetrieveCaptureRequestOrCameraMetadataViaInterface() {
        val metadata = FakeResultMetadata(
            resultMetadata = mapOf(CaptureResult.JPEG_QUALITY to 95),
            extraResultMetadata = mapOf(FakeMetadata.TEST_KEY to 42)
        )

        assertThat(metadata[FakeMetadata.TEST_KEY]).isNotNull()
        assertThat(metadata[FakeMetadata.TEST_KEY_ABSENT]).isNull()

        assertThat(metadata[CaptureResult.JPEG_QUALITY]).isNotNull()
        assertThat(metadata[CaptureResult.COLOR_CORRECTION_MODE]).isNull()
    }
}
