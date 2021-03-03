package com.example.demo.model

import com.example.demo.util.isNull
import com.example.demo.util.notNull
import javax.persistence.Column
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractEntity {

    @Column(nullable = false, unique = true)
    var uid: String? = null

    @javax.persistence.Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun hashCode()  = uid?.hashCode() ?: super.hashCode()

    override fun equals(other: Any?): Boolean {
        this === other && return true
        return other is AbstractEntity
                &&  uid.notNull
                && javaClass == other.javaClass
                && uid == other.uid
    }

}

enum class Gender {
    WOMAN, MAN, OTHER
}