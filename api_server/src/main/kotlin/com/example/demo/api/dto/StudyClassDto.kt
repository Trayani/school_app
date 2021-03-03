package com.example.demo.api.dto

import com.example.demo.api.*
import com.example.demo.util.asSet
import com.example.demo.repo.dbCtxt
//import com.example.demo.repo.findClassroom
//import com.example.demo.repo.findStudent
//import com.example.demo.repo.findTeacher
import com.example.demo.model.StudyClass


class StudyClassDto : AbstractDto<StudyClass>() {
    var name: String? = null
    var yearOfStudy: Int = 0
    var classroomUid: String? = null
    var classTeacherUid: String? = null
    var studentUids: MutableSet<String>? = null

    override fun toRawEnt(): StudyClass {
        val teacher = classTeacherUid?.let { dbCtxt.findTeacher(it) }
        val students = studentUids?.map { dbCtxt.findStudent(it) }.asSet()
        val room = classroomUid?.let { dbCtxt.findClassroom(it) }
        return StudyClass(name!!, 0, teacher, students, room)
    }


    override fun mapFromEntity(ent: StudyClass) {
        studentUids = ent.getStudents().toUidSet()
    }
}

fun StudyClass.toDto() = this.toDtoUnchecked<StudyClass, StudyClassDto>()