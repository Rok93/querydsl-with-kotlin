package com.example.querydslwithkotlin.entity

import org.springframework.data.jpa.repository.JpaRepository

interface HelloRepository: JpaRepository<Hello, Long> {
}
