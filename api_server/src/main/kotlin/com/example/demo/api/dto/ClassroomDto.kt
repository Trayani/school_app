package com.example.demo.api.dto

import com.example.demo.api.AbstractDto
import com.example.demo.api.toDtoUnchecked
import com.example.demo.repo.dbCtxt
//import com.example.demo.repo.findStudent
//import com.example.demo.repo.findStudyClass
import com.example.demo.model.Classroom
import com.example.demo.model.Student

val defaultRoomCapacity = 25

class ClassroomDto : AbstractDto<Classroom>() {

    var name: String? = null
    var studentSeatsUid: MutableMap<String, Int>? = null
    var owningClassUid: String? = null
    var capacity: Int? = defaultRoomCapacity


    override fun mapFromEntity(ent: Classroom) {
        studentSeatsUid = ent.getStudentSeats().map { it.key.uid!! to it.value }.toMap().toMutableMap()
    }

    override fun toRawEnt(): Classroom {
        val studentSeats = mutableMapOf<Student, Int>()
        studentSeatsUid?.forEach { stuId, seatIdx ->
            val student = dbCtxt.findStudent(stuId)
            studentSeats[student] = seatIdx
        }
        val owningClass = owningClassUid?.let { dbCtxt.findStudyClass(it) }
        return Classroom(name!!, capacity!!, studentSeats, owningClass)
    }

}

fun Classroom.toDto() = this.toDtoUnchecked<Classroom, ClassroomDto>()