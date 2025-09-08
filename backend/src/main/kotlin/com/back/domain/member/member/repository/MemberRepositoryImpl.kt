package com.back.domain.member.member.repository

import com.back.domain.member.member.entity.Member
import com.back.domain.member.member.entity.QMember
import com.back.domain.member.member.entity.QMember.member
import com.back.standard.extensions.getOrThrow
import com.back.standard.search.MemberSearchKeywordType
import com.back.standard.util.QueryDslUtil
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.hibernate.Session
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class MemberRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val entityManager: EntityManager
) : MemberRepositoryCustom {
    override fun findByKeyword(keywordType: MemberSearchKeywordType, keyword: String, pageable: Pageable): Page<Member> {
        val builder = BooleanBuilder()

        if (keyword.isNotBlank()) {
            applyKeywordFilter(keywordType, keyword, builder)
        }

        // query
        val membersQuery = createMembersQuery(builder)

        // sort
        applySorting(pageable, membersQuery)

        // paging
        membersQuery.offset(pageable.offset).limit(pageable.pageSize.toLong())

        // total
        val totalQuery = createTotalQuery(builder)

        return PageableExecutionUtils.getPage(membersQuery.fetch(), pageable) { totalQuery.fetchOne().getOrThrow() }
    }

    private fun applyKeywordFilter(kwType: MemberSearchKeywordType, kw: String, builder: BooleanBuilder) {
        when (kwType) {
            MemberSearchKeywordType.USERNAME -> builder.and(member.username.containsIgnoreCase(kw))
            MemberSearchKeywordType.NICKNAME -> builder.and(member.nickname.containsIgnoreCase(kw))
            MemberSearchKeywordType.ALL -> builder.and(
                member.username.containsIgnoreCase(kw)
                    .or(member.nickname.containsIgnoreCase(kw))
            )
        }
    }

    private fun createMembersQuery(builder: BooleanBuilder): JPAQuery<Member> {
        return jpaQueryFactory
            .selectFrom(QMember.member)
            .where(builder)
    }

    private fun applySorting(pageable: Pageable, membersQuery: JPAQuery<Member>) {
        QueryDslUtil.applySorting(membersQuery, pageable) {
            when (it) {
                "id" -> member.id
                "username" -> member.username
                "nickname" -> member.nickname
                else -> null
            }
        }
    }

    private fun createTotalQuery(builder: BooleanBuilder): JPAQuery<Long> {
        return jpaQueryFactory
            .select(QMember.member.count())
            .from(QMember.member)
            .where(builder)
    }


    override fun findByUsername(username: String): Member? {
        return entityManager.unwrap(Session::class.java)
            .byNaturalId(Member::class.java)
            .using(Member::username.name, username)
            .load()
    }


    // 실습
    override fun findQById(id: Int): Member? = jpaQueryFactory
            .selectFrom(member)
            .where(member.id.eq(id))
            .fetchOne()

    override fun findQByUsername(username: String): Member? = jpaQueryFactory
            .selectFrom(member)
            .where(member.username.eq(username))
            .fetchOne()

    override fun findQByIdIn(ids: List<Int>): List<Member> = jpaQueryFactory
            .selectFrom(member)
            .where(member.id.`in`(ids))
            .fetch()

    override fun findQByUsernameAndNickname(username: String, nickname: String): Member? = jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(member.nickname.eq(nickname))
            )
            .fetchOne()

    override fun findQByUsernameOrNickname(username: String, nickname: String): List<Member> = jpaQueryFactory
            .selectFrom(member)
            .where(
                QMember.member.username.eq(username)
                    .or(member.nickname.eq(nickname))
            )
            .fetch()

    override fun findQByUsernameAndEitherPasswordOrNickname(username: String, password: String?, nickname: String?): List<Member> = jpaQueryFactory
            .selectFrom(member)
            .where(
                member.username.eq(username)
                    .and(
                        member.password.eq(password)
                            .or(member.nickname.eq(nickname))
                    )
            )
            .fetch()

    override fun findQByNicknameContaining(nickname: String): List<Member> = jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .fetch()

    override fun countQByNicknameContaining(nickname: String): Long = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(member.nickname.contains(nickname))
            .fetchOne() ?: 0L

    override fun existsQByNicknameContaining(nickname: String): Boolean = jpaQueryFactory
            .selectOne()
            .from(member)
            .where(member.nickname.contains(nickname))
            .fetchFirst() != null

    override fun findQByNicknameContaining(nickname: String, pageable: Pageable): Page<Member> {
        val results = jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalQuery = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(member.nickname.contains(nickname))

        return PageableExecutionUtils.getPage(results, pageable) {
            totalQuery.fetchFirst() ?: 0L
        }
    }

    override fun findQByNicknameContainingOrderByIdDesc(nickname: String): List<Member> = jpaQueryFactory
            .selectFrom(member)
            .where(member.nickname.contains(nickname))
            .orderBy(member.id.desc())
            .fetch()

    override fun findQByUsernameContaining(username: String, pageable: Pageable): Page<Member> {
        val query = jpaQueryFactory
            .selectFrom(member)
            .where(member.username.contains(username))

        // Apply sorting
        applySorting(pageable, query)

        // Apply paging
        val results = query
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // total
        val totalQuery = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(member.username.contains(username))

        return PageableExecutionUtils.getPage(results, pageable) {
            totalQuery.fetchFirst() ?: 0L
        }
    }
}