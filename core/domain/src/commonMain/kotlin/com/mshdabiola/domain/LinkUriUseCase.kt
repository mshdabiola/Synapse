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
package com.mshdabiola.domain

import com.mshdabiola.model.note.NoteLink

private val regex =
    "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

class LinkUriUseCase {
    operator fun invoke(detail: String, take: Int): List<NoteLink> {
        return if (detail.contains(regex.toRegex())) {
            detail.split("\\s".toRegex())
                .filter { it.trim().matches(regex.toRegex()) }
                .mapIndexed { index, s ->
                    val path = getAuthorityFromUrl(s) ?: ""
                    val icon = "https://icon.horse/icon/$path"
                    NoteLink(
                        id = index,
                        icon = icon,
                        path = path,
                        url = s,
                    )
                }
                .take(take)
        } else {
            emptyList()
        }
    }

    fun getAuthorityFromUrl(urlString: String?): String? {
        if (urlString == null || urlString.isEmpty()) {
            return null
        }

        // 1. Find the scheme (e.g., "http://", "https://")
        var schemeEndIndex = urlString.indexOf("://")
        if (schemeEndIndex == -1) {
            // Might be a relative URL or a URL without a scheme,
            // or a scheme-relative URL like "//example.com"
            if (urlString.startsWith("//")) {
                schemeEndIndex = 0 // Treat as start of authority
            } else {
                // Cannot reliably find authority without a scheme or "//"
                return null
            }
        } else {
            schemeEndIndex += 3 // Move past "://"
        }

        // 2. Find the end of the authority part
        // Authority ends at the next '/', '?', or '#'
        var authorityEndIndex = -1
        val pathStartIndex = urlString.indexOf('/', schemeEndIndex)
        val queryStartIndex = urlString.indexOf('?', schemeEndIndex)
        val fragmentStartIndex = urlString.indexOf('#', schemeEndIndex)

        if (pathStartIndex != -1) {
            authorityEndIndex = pathStartIndex
        }

        if (queryStartIndex != -1) {
            if (authorityEndIndex == -1 || queryStartIndex < authorityEndIndex) {
                authorityEndIndex = queryStartIndex
            }
        }

        if (fragmentStartIndex != -1) {
            if (authorityEndIndex == -1 || fragmentStartIndex < authorityEndIndex) {
                authorityEndIndex = fragmentStartIndex
            }
        }

        if (authorityEndIndex == -1) {
            // If no path, query, or fragment, the rest of the string is the authority
            return urlString.substring(schemeEndIndex)
        } else {
            return urlString.substring(schemeEndIndex, authorityEndIndex)
        }
    }
}
