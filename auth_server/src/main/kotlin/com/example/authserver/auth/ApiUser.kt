package com.example.authserver.auth

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Entity
class ApiUser(
        @Column(nullable = false, unique = true)
        var userName : String? = null,

        @Column(nullable = false)
        var password : String? = null,

        @Column(nullable = false)
        var role : String ? = null
)  {


    @javax.persistence.Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun hashCode()  = this.userName?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean {
        this === other && return true
        return other is AbstractEntity
                &&  this.userName != null
                && javaClass == other.javaClass
                && this.userName == other.uid
    }
}

object UserAuth {
    val STUDENT = "STUDENT"
    val TEACHER= "TEACHER"
    val ADMIN= "ADMIN"
    val PRINCIPAL = "PRINCIPAL"
    val DEPUTY = "DEPUTY"
}