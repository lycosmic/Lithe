package io.github.lycosmic.lithe.data.mapper

import io.github.lycosmic.lithe.data.local.entity.AuthorizedDirectory
import io.github.lycosmic.model.Directory


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