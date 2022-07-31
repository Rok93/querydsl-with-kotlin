package com.example.querydslwithkotlin.entity

import javax.persistence.*

@Entity
class Member(
    val username: String?,
    val age: Int = 0, // TODO 2022-07-28 경록: Inline Class로 변경해보기!
    team: Team? = null,
) {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private val id: Long = 0L

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null

    init {
        if (team != null) {
            changeTeam(team)
        }
    }

    fun changeTeam(team: Team) {
        this.team = team
        team.addMember(this)
    }
}
