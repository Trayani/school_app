package com.example.demo.model

import org.hibernate.envers.Audited
import java.util.*
import javax.persistence.*


@Audited
@Entity
class Student (var name : String,
               var surname: String ,
               var birthDate: Date,
               var gender: Gender? = null,
               var joinedDate: Date =  Date(System.currentTimeMillis()) ,
               _studyClass: StudyClass? ,
               var status : StudentStatus = StudentStatus.ENROLLED ,

               @OneToMany(cascade = [CascadeType.ALL ])
               @JoinTable(name = "subject_grades_mapping",
                       joinColumns = [JoinColumn(name = "student_id", referencedColumnName = "id")],
                       inverseJoinColumns = [JoinColumn(name = "grade_id", referencedColumnName = "id") ])
               @MapKeyJoinColumn(name = "subject_id")
               private var currentGrades: MutableMap<Subject, SubjectGrading > = mutableMapOf(),
               var picture : ByteArray? = null
) : AbstractEntity() {

    @ManyToOne( fetch = FetchType.LAZY , cascade = [CascadeType.ALL])
    var studyClass : StudyClass ? = null
        set (value) {
            value == field && return
            val old = field
            field = value
            old?.removeStudent(this)
            field?.addStudent(this)
        }

    init {    studyClass = _studyClass    }

    fun addGrade(subj : Subject, grade : ExamGrade) {
        currentGrades.computeIfAbsent(subj) { SubjectGrading() }.grades.add(grade )
    }
    fun getCurrentGrades() = currentGrades.toMap()
}

enum class StudentStatus {ENROLLED , ACTIVE, GRADUATED, EXPELLED}