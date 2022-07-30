package com.example.querydslwithkotlin.entity

import java.util.ArrayList
import javax.persistence.*

@Entity
class Team(
    name: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private val id: Long = 0L

    @Column(name = "name")
    val name: String = name

    @OneToMany(mappedBy = "team")
    val members: MutableList<Member> = ArrayList()

    fun addMember(member: Member) {
        members.add(member)
    }
}
