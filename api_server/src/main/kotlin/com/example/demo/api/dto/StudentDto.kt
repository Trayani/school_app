package com.example.demo.api.dto

import com.example.demo.api.AbstractDto
import com.example.demo.api.toDtoUnchecked
//import com.example.demo.api.dto.ExamGradeDto.Companion.toDto
//import com.example.demo.api.dto.ExamGradeDto.Companion.toDtoList
import com.example.demo.repo.dbCtxt
//import com.example.demo.repo.findStudyClass
//import com.example.demo.repo.findSubject
import com.example.demo.model.*
import java.util.*
import kotlin.collections.ArrayList


class StudentDto : AbstractDto<Student>() {
    var name: String? = null
    var surname: String? = null
    var birthDate: Date? = null
    var gender: Gender? = null
    var joinedDate: Date? = null // =  Date(System.currentTimeMillis())
    var studyClassUid: String? = null
    var grades: MutableMap<String, ArrayList<ExamGradeDto>>? = null
    var picture: ByteArray? = null
    var status: StudentStatus = StudentStatus.ENROLLED

    override fun mapFromEntity(ent: Student) {
        grades = ent.getCurrentGrades().map { it.key.uid!! to ArrayList (it.value.toDtoList()) }.toMap().toMutableMap()
    }

    override fun toRawEnt(): Student {
        val studyClass = studyClassUid?.let { dbCtxt.findStudyClass (it) }
        val grades = mutableMapOf<Subject, SubjectGrading>()

        this.grades?.forEach { subUid, grade ->
            val subjGrades = grade.map { it.toRawEnt() }.toMutableList()
            val subj = dbCtxt.findSubject(subUid)
            grades[subj] = SubjectGrading(subjGrades)
        }

        return Student(name!!, surname!!, birthDate!!, gender, joinedDate ?: Date(System.currentTimeMillis())
                , studyClass, status, grades, picture )
    }

}

fun Student.toDto() = this.toDtoUnchecked<Student, StudentDto>()