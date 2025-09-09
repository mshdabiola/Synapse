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

private val URL_REGEX = Regex("""(?i)\bhttps?://[^\s<>"')\]]+""")

class LinkUriUseCase {
    operator fun invoke(detail: String, take: Int = 10): List<NoteLink> {
        if (take <= 0) return emptyList()
        return URL_REGEX
            .findAll(detail)
            .map { it.value.trimEnd('.', ',', ';', ')', ']', '"', '\'') }
            .take(take)
            .mapIndexed { index, s ->
                val host = getAuthorityFromUrl(s).orEmpty()
                val icon = if (host.isNotEmpty()) "https://icon.horse/icon/$host" else ""
                NoteLink(
                    id = index,
                    icon = icon,
                    path = host,
                    url = s,
                )
            }
            .toList()
    }

    private fun getAuthorityFromUrl(urlString: String?): String? {
        if (urlString == null || urlString.isEmpty()) {
            return null
        }

        var schemeEndIndex = urlString.indexOf("://")
        schemeEndIndex = when {
            schemeEndIndex >= 0 -> schemeEndIndex + 3
            urlString.startsWith("//") -> 2 // scheme-relative
            else -> return null
        }

        val end = listOf(
            urlString.indexOf('/', schemeEndIndex),
            urlString.indexOf('?', schemeEndIndex),
            urlString.indexOf('#', schemeEndIndex),
        ).filter { it >= 0 }.minOrNull() ?: urlString.length

        var authority = urlString.substring(schemeEndIndex, end)
        val at = authority.lastIndexOf('@')
        if (at != -1) authority = authority.substring(at + 1) // drop userinfo

        // Strip port if present (keep IPv6 literal)
        authority = if (authority.startsWith("[")) {
            val close = authority.indexOf(']')
            if (close != -1) authority.substring(0, close + 1) else authority
        } else {
            authority.substringBefore(':')
        }

        return authority.ifEmpty { null }
    }
}
