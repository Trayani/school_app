package com.example.demo.api.dto

import com.example.demo.api.*
import com.example.demo.util.asSet
import com.example.demo.repo.dbCtxt
//import com.example.demo.repo.findStudyClass
//import com.example.demo.repo.findSubject
import com.example.demo.model.*

class TeacherDto : AbstractDto<Teacher>(){
    var name : String? = null
    var surname: String? = null
    var studyClassUid: String? = null
    var gender: Gender? = null
    var subjectUid : MutableSet<String>? = null


    override fun toRawEnt(): Teacher {
        val stClass = studyClassUid?.let { dbCtxt.findStudyClass (it) }
        val subjects = subjectUid?.map {  dbCtxt.findSubject (it) }.asSet()
        return Teacher (name!!, surname!!, stClass, gender, subjects )
    }

    override fun mapFromEntity(ent: Teacher) {
        subjectUid = ent.getSubjectsSet().toUidSet()
    }
}

fun Teacher.toDto() = this.toDtoUnchecked<Teacher, TeacherDto>()