package com.example.demo.model

import org.hibernate.envers.Audited
import java.sql.Timestamp
import javax.persistence.*

@Audited
@Entity
class ExamGrade (
        var grade : Int,
        var submittedTs : Timestamp,
        @ManyToOne( fetch = FetchType.LAZY , cascade = [CascadeType.ALL])
        var teacher: Teacher,
        var note: String?
) {
        @javax.persistence.Id
        @GeneratedValue (strategy = GenerationType.IDENTITY)
        var id: Long? = null
}