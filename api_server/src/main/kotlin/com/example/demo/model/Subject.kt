package com.example.demo.model

import com.example.demo.util.asSet
import org.hibernate.envers.Audited
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.ManyToMany

@Audited
@Entity
class Subject(var name: String,
              var optional: Boolean = false,

              @ManyToMany(mappedBy = "subjects")
              private var teachers: MutableSet<Teacher> = mutableSetOf(),

              @ElementCollection
              var yearsOfStudy: MutableSet<Int> = mutableSetOf()
) : AbstractEntity() {
    init {
        teachers.forEach { it.addSubject(this) }
    }

    fun addTeacher(teacher: Teacher) {
        if (teachers.add(teacher))
            teacher.addSubject(this)
    }

    fun removeTeacher(teacher: Teacher) {
        if (teachers.remove(teacher))
            teacher.removeSubject(this)
    }

    fun getTeachers() = teachers.toList()
}