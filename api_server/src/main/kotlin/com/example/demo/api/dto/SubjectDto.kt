package com.example.demo.api.dto

import com.example.demo.api.AbstractDto
import com.example.demo.api.toDtoUnchecked
//import com.example.demo.repo.findTeacher
import com.example.demo.api.toUidSet
import com.example.demo.util.asSet
import com.example.demo.repo.dbCtxt
import com.example.demo.model.Subject


class SubjectDto : AbstractDto<Subject>(){
    var name: String? = null
    var optional: Boolean = false
    var teacherUid : MutableSet<String>? = null
    var yearsOfStudy : MutableSet<Int>? = null

    override fun toRawEnt(): Subject {
        val teachers = teacherUid?.map { dbCtxt.findTeacher(it) }.asSet()
        return Subject( name!!, optional, teachers, yearsOfStudy ?: mutableSetOf() )
    }

    override fun mapFromEntity(ent: Subject) {
        teacherUid = ent.getTeachers().toUidSet()
    }
}

fun Subject.toDto() = this.toDtoUnchecked<Subject, SubjectDto>()