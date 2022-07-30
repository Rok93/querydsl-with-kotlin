package com.example.querydslwithkotlin

import com.example.querydslwithkotlin.entity.Hello
import com.example.querydslwithkotlin.entity.Member
import com.example.querydslwithkotlin.entity.QHello
import com.example.querydslwithkotlin.entity.QMember
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Transactional
@SpringBootTest
internal class QuerydslWithKotlinApplicationTest {
    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    internal fun queryDslTestWithJUnit5() {
        // given
        val hello = Hello(content = "hello")
        entityManager.persist(hello)

        // when
        val query = JPAQueryFactory(entityManager)
        val qHello = QHello("h")

        // then
        val result = query.selectFrom(qHello)
            .fetchOne()

        Assertions.assertThat(result).isSameAs(hello)
    }

    @Test
    internal fun startQuerydsl() {
        // given
        entityManager.persist(
            Member(
                username = "member1",
                age = 20,
                team = null
            )
        )
        entityManager.flush()
        entityManager.clear()

        val queryFactory = JPAQueryFactory(entityManager)
        val m = QMember("m")

        val findMember = queryFactory.selectFrom(m)
            .where(m.username.eq("member1"))
            .fetchOne()

        assertThat(findMember!!.username).isEqualTo("member1")
    }
}
