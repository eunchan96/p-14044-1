package com.back.standard.search

import com.back.standard.extensions.toCamelCase
import org.springframework.data.domain.Sort

enum class MemberSearchSortType {
    ID,
    ID_ASC,
    NICKNAME,
    NICKNAME_ASC,
    USERNAME,
    USERNAME_ASC,
    CREATED_AT,
    CREATED_AT_ASC;

    val isAsc = name.endsWith("_ASC")

    val property = name.removeSuffix("_ASC").toCamelCase()

    val direction = if (isAsc) Sort.Direction.ASC else Sort.Direction.DESC

    val sortBy = Sort.by(direction, property)
}