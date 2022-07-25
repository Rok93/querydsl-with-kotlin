package com.example.querydslwithkotlin

import com.example.querydslwithkotlin.entity.Hello
import com.example.querydslwithkotlin.entity.QHello
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions
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
}
