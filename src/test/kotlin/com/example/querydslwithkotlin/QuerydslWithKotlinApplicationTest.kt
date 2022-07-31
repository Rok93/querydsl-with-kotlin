package com.example.querydslwithkotlin

import com.example.querydslwithkotlin.entity.Hello
import com.example.querydslwithkotlin.entity.Member
import com.example.querydslwithkotlin.entity.QHello
import com.example.querydslwithkotlin.entity.QMember.*
import com.querydsl.jpa.impl.JPAQueryFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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

    /**
     * QueryFactory를 필드 레벨로 가져가도 괜찮음.
     * 동시성 문제 같은 것에 대한 고민 안해도 된다!
     * -> 동시에 여러 쓰레드에서 entityManager에 접근하게 되면, 어떻게 될까...?
     * 스프링 프레임워크가 주입해주는 em 자체가 멀티 스레드에 아무 문제가 없도록 설계되어 있음.
     * 여러 멀티 스레드에서 접근해도 현재는 트랜잭션이 어디에 걸려있는지에 따라 트랜잭션에 걸리도록 되어있음.
     */
    private lateinit var queryFactory: JPAQueryFactory

    @BeforeEach
    internal fun setUp() {
        queryFactory = JPAQueryFactory(entityManager)

        entityManager.persist(
            Member(
                username = "member1",
                age = 10,
                team = null
            )
        )

        entityManager.persist(
            Member(
                username = "member2",
                age = 20,
                team = null
            )
        )

        entityManager.flush()
        entityManager.clear()
    }

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

        assertThat(result).isSameAs(hello)
    }

    @Test
    internal fun startQuerydsl() {
        val findMember = queryFactory.selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne()

        assertThat(findMember!!.username).isEqualTo("member1")
    }

    @Test
    internal fun search() {
        val findMember = queryFactory.selectFrom(member)
            .where(
                member.username.eq("member1"),
//                    .and(member.age.eq(10))
                member.age.eq(10) // ','은 and 조건과 동일함!
// where 조건에 'null' 값이 들어가면, 그 조건은 없는 것으로 처리함! (동적 쿼리 작성 가능!)
            ).fetchOne()!!

        assertThat(findMember.username).isEqualTo("member1")
        assertThat(findMember.age).isEqualTo(10)
    }
}
