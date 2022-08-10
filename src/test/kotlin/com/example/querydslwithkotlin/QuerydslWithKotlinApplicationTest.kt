package com.example.querydslwithkotlin

import com.example.querydslwithkotlin.entity.Hello
import com.example.querydslwithkotlin.entity.Member
import com.example.querydslwithkotlin.entity.QHello
import com.example.querydslwithkotlin.entity.QMember.*
import com.example.querydslwithkotlin.entity.QTeam.team
import com.example.querydslwithkotlin.entity.Team
import com.querydsl.core.Tuple
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

        val teamA = Team("teamA")
        entityManager.persist(
            teamA
        )

        val teamB = Team("teamB")
        entityManager.persist(
            teamB
        )

        entityManager.persist(
            Member(
                username = "member1",
                age = 10,
                team = teamA
            )
        )

        entityManager.persist(
            Member(
                username = "member2",
                age = 20,
                team = teamB
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

    @Test
    internal fun resultFetch() {
        val fetch = queryFactory.selectFrom(member)
            .fetch()

//        val fetchOne = queryFactory.selectFrom(member)
//            .fetchOne()

        val fetchFirst = queryFactory.selectFrom(member)
            .fetchFirst() // limit(1).fetchOne() 과 동일!

        val fetchResults = queryFactory.selectFrom(member)
            .fetchResults() // 쿼리가 두번나감.

        val fetchCount = queryFactory.selectFrom(member)
            .fetchCount()
    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 (DESC)
     * 2. 회원 이름 올림차순 (ASC)
     * 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    internal fun sort() {
        entityManager.persist(Member(null, age = 100))
        entityManager.persist(Member("member5", age = 100))
        entityManager.persist(Member("member6", age = 100))

        val result = queryFactory.selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(
                member.age.desc(),
                member.username.asc().nullsLast()
            )
            .fetch()

        assertThat(result[0].username).isEqualTo("member5")
        assertThat(result[1].username).isEqualTo("member6")
        assertThat(result[2].username).isNull()
    }

    @Test
    internal fun paging1() {
        val result = queryFactory.selectFrom(member)
            .orderBy(member.username.desc())
            .offset(0)
            .limit(2)
            .fetch()

        assertThat(result).hasSize(2)
    }

    @Test
    internal fun paging2() {
        val queryResults = queryFactory.selectFrom(member)
            .orderBy(member.username.desc())
            .offset(0)
            .limit(2)
            .fetchResults()

        assertThat(queryResults.total).isEqualTo(2)
        assertThat(queryResults.limit).isEqualTo(2)
        assertThat(queryResults.offset).isEqualTo(0)
        assertThat(queryResults.results).hasSize(2)
    }

    @Test
    internal fun aggregation() {
        // 내가 조회하고 싶은 값들만 정해서 조회를 하면, Tuple 타입으로 결과 값이 나온다. (참고, Tuple은 Querydsl의 Tuple임)
        // 이런 경우에는 DTO로 뽑을 수도 있음. 실무에서는 DTO를 사용하는 방식을 더 많이 사용함.
        val result: MutableList<Tuple> = queryFactory
            .select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
            )
            .from(member)
            .fetch()

        // tuple은 여러개의 타입이 있을 때, 꺼내올 수 있는 것
        val tuple = result[0]
        assertThat(tuple[member.count()]).isEqualTo(2)
        assertThat(tuple[member.age.sum()]).isEqualTo(30)
        assertThat(tuple[member.age.avg()]).isEqualTo(15.0)
        assertThat(tuple[member.age.max()]).isEqualTo(20)
        assertThat(tuple[member.age.min()]).isEqualTo(10)
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구하라!
     */
    @Test
    internal fun group() {
        val result = queryFactory.select(
            team.name,
            member.age.avg()
        )
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch()

        val teamA = result[0]
        val teamB = result[1]

        assertThat(teamA[team.name]).isEqualTo("teamA")
        assertThat(teamA[member.age.avg()]).isEqualTo(10.0)

        assertThat(teamB[team.name]).isEqualTo("teamB")
        assertThat(teamB[member.age.avg()]).isEqualTo(20.0)
    }

    /*
    groupBy 예시

    .groupBy(item.price)
    .having(item.price.gt(1_000))
    ...
     */
}
