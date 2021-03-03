package com.example.demo.model

import org.hibernate.envers.Audited
import javax.persistence.*


@Audited
@Entity
class StudyClass(
        @Column(unique = true, nullable = false)
        var name: String = "",

        @Column(nullable = false)
        var yearOfStudy: Int = 0,
        _classTeacher: Teacher? = null,
        _students: MutableSet<Student> = mutableSetOf(),
        _classroom: Classroom? = null
)    : AbstractEntity() {
    @OneToOne( fetch = FetchType.LAZY ,  mappedBy = "owningClass" ,    optional = true   )
    @JoinColumn(name = "room_id" )
    var classroom : Classroom? =  null
        set(value) {
            field == value && return
            val old = field
            field = value
            old?.let { if (it.owningClass == this) it.owningClass = null  }
            field?.let { if (it.owningClass != this ) it.owningClass = this }
        }

    @OneToOne( fetch = FetchType.LAZY ,    optional = true   )
    @JoinColumn(name = "teacher_id" )
    var classTeacher : Teacher? =  _classTeacher
        set(value) {
            field == value && return
            val old = field
            field = value
            old?.let { if (it.studyClass == this) it.studyClass = null  }
            field?.let { if (it.studyClass != this ) it.studyClass = this }
        }

    @OneToMany( mappedBy = "studyClass")
    private var students : MutableSet<Student> = mutableSetOf()

    fun addStudent(student: Student) {
        hasStudent(student) && return
        if ( student.studyClass != this) {
            student.studyClass?.removeStudent(student)
        }
        students.add(student)
        student.studyClass = this
    }

    fun removeStudent(student: Student) {
        if ( student.studyClass == this)
            student.studyClass = null
        students.remove(student)
    }
    fun getStudents() = students.toList()
    fun hasStudent(student: Student) = students.contains(student)

    init {
        classroom = _classroom
        students = _students
        classTeacher = _classTeacher
        _classroom?.owningClass = this
        classTeacher?.studyClass = this
    }

}