package com.back.domain.post.postUser.entity

import com.back.global.jpa.entity.BaseTime
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["subject_id", "name"])
    ]
)
class PostUserAttr(
    subject: PostUser,
    name: String,
    value: String,
) : BaseTime() {
    @field:ManyToOne(fetch = LAZY)
    @field:JoinColumn(name = "subject_id")
    val subject = subject

    val name = name

    @field:Column(name = "val", columnDefinition = "TEXT")
    var value: String = value
}