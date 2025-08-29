package com.mshdabiola.domain

import com.mshdabiola.model.note.NoteLink

private val regex =
    "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)"

class LinkUriUseCase  {
    operator fun invoke(detail: String, take: Int): List<NoteLink> {
        return if (detail.contains(regex.toRegex())) {
            detail.split("\\s".toRegex())
                .filter { it.trim().matches(regex.toRegex()) }
                .mapIndexed { index, s ->
                    val path = s.toUri().authority ?: ""
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
}
