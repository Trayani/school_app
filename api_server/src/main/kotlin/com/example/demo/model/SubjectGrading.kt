package com.example.demo.model

import org.hibernate.envers.Audited
import javax.persistence.*

@Audited
@Entity
class SubjectGrading(
    @OneToMany(cascade = [CascadeType.ALL])
    val grades: MutableList<ExamGrade> = mutableListOf()) {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    val averageGrade
        get() = grades.sumBy { it.grade } / grades.size

}
