package com.example.demo.api.dto

import com.example.demo.repo.dbCtxt
//import com.example.demo.repo.findTeacher
import com.example.demo.model.ExamGrade
import com.example.demo.model.SubjectGrading
import java.sql.Timestamp


class ExamGradeDto
{
    var grade : Int? =null
    var submittedTs : Timestamp? =null
    var teacherUid: String? =null
    var note: String? =null

     fun toRawEnt(): ExamGrade {
        val teacher = dbCtxt.findTeacher (teacherUid!!)
        return ExamGrade(grade!!, submittedTs!!, teacher  , note  )
    }
}

fun ExamGrade.toDto() = dbCtxt.modelMapper.map(this, ExamGradeDto::class.java )
fun SubjectGrading.toDtoList() = grades.map { it.toDto() }.toMutableList()