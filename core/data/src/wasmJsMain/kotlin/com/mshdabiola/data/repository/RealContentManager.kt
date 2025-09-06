/*
 * Designed and developed by 2024 mshdabiola (lawal abiola)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mshdabiola.data.repository

class RealContentManager() : ContentManager {

    override fun saveImage(uri: String): String {
        println("Warning: saveImage (WasmJS) called for uri: $uri. No actual file saving. Returning timestamp.")
        // In a real WasmJS app, this would involve JS interop to handle the data (e.g., from a blob URI or data URL)
        // and store it, perhaps in IndexedDB.
        return ""
    }

    override fun saveVoice(uri: String): String {
        println("Warning: saveVoice (WasmJS) called for uri: $uri. No actual file saving. Returning timestamp.")
        // Similar to saveImage, would require JS interop.
        return ""
    }

    override fun pictureUri(): String {
        println(
            "Warning: pictureUri (WasmJS) called. Returning placeholder. " +
                "Actual URI generation needs JS interop for camera/file input.",
        )
        // This would typically involve JS interop to trigger a file input or camera access.
        return "wasmjs:new_image_placeholder_1"
    }

//    override fun getImagePath(data: Long): String {
//        // These paths are identifiers, not actual file system paths in WasmJS context without a virtual FS.
//        return "wasmjs-data/image_$data.jpg"
//    }
//
//    override fun getVoicePath(data: Long): String {
//        return "wasmjs-data/voice_$data.amr"
//    }

    override fun dataFile(drawingId: Long): String {
        return "wasmjs-data/drawing_$drawingId.json"
    }

    override fun getAudioLength(path: String): Long {
        println(
            "Warning: getAudioLength (WasmJS) called for path: $path." +
                " Audio metadata not available in pure WasmJS. Returning 0L.",
        )
        // Would require JS interop with Web Audio API or a Wasm-compiled media library.
        return 0L
    }

    override fun imageToText(path: String): String {
        println(
            "Warning: WasmJsPlaceholderImageToText.toText called for path: $path. " +
                "OCR not available in pure WasmJS. Returning empty string.",
        )
        return ""
    }
}
