package com.back.global.jpa.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is BaseTime) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}