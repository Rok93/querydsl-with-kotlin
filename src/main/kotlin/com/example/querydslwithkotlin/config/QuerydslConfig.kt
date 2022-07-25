package com.example.querydslwithkotlin.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class QuerydslConfig(
    val entityManaeger: EntityManager
) {
    @Bean
    fun jpaQueryFactory() = JPAQueryFactory(entityManaeger)
}
