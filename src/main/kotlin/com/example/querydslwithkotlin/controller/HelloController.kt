package com.example.querydslwithkotlin.controller

import com.example.querydslwithkotlin.entity.Hello
import com.example.querydslwithkotlin.entity.HelloRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController(
    private val helloRepository: HelloRepository,
) {
    @GetMapping("/read")
    @Transactional(readOnly = true)
    fun read(@RequestParam id: Long): Hello {
        return helloRepository.findByIdOrNull(id) ?: Hello(content = "존재하지 않습니다.")
    }

    @GetMapping("/write")
    @Transactional
    fun write(@RequestParam content: String): Hello {
        return helloRepository.save(Hello(content = content))
    }
}
