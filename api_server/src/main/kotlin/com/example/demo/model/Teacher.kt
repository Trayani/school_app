package com.example.demo.model

import org.hibernate.envers.Audited
import javax.persistence.*

enum class Authorisation {
    TEACHER, PRINCIPAL, DEPUTY
}


@Audited
@Entity
 class Teacher (var name : String = "",
                var surname: String  = "",
                @OneToOne( fetch = FetchType.LAZY , mappedBy = "classTeacher" , cascade = [CascadeType.ALL] )
                var studyClass: StudyClass ?  = null,
                var gender: Gender? = null,

                @ManyToMany( fetch = FetchType.LAZY , cascade = [CascadeType.ALL])
                @JoinColumn(name = "subject_id" )
                var subjects : MutableSet<Subject> = mutableSetOf(),
                var auth : Authorisation = Authorisation.TEACHER
) : AbstractEntity() {
    fun addSubject(sub:Subject) {
        if (subjects.add(sub))
            sub.addTeacher(this)
    }

    fun removeSubject(sub: Subject){
        if (subjects.remove(sub))
            sub.removeTeacher(this)
    }

    fun getSubjectsSet() = subjects.toSet()

    init {
        subjects.forEach { it.addTeacher(this) }
    }
}