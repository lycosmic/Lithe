package io.github.lycosmic.data.mapper

import io.github.lycosmic.data.local.entity.AuthorizedDirectory
import io.github.lycosmic.domain.model.Directory


fun AuthorizedDirectory.toDomain(): Directory {
    return Directory(
        id = id,
        uriString = uri,
        root = root,
        path = path,
        addTime = addTime
    )
}

fun Directory.toEntity(): AuthorizedDirectory {
    return AuthorizedDirectory(
        id = id,
        uri = uriString,
        root = root,
        path = path,
        addTime = addTime
    )
}