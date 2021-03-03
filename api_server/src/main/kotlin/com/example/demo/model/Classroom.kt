package com.example.demo.model

import com.example.demo.api.dto.defaultRoomCapacity
import com.example.demo.util.so
import org.hibernate.envers.Audited
import javax.persistence.*


@Audited
@Entity
class Classroom(
        @Column(unique = true, nullable = false)
        var name: String = "",

        var capacity: Int = defaultRoomCapacity,

        @ElementCollection
        @CollectionTable(name = "student_seats", joinColumns = [JoinColumn(name = "room_id", referencedColumnName = "id")])
        @MapKeyColumn(name = "student_id")
        private var studentSeats: MutableMap<Student, Int> = mutableMapOf(),
        _owningClass: StudyClass? = null
) : AbstractEntity() {

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var owningClass: StudyClass? = null
        set(value) {
            field == value && return
            val old = field
            field = value
            old?.classroom == this && so { old.classroom = null }
            field?.let {
                if (it.classroom != this)
                    field!!.classroom = this
            }
        }

    init {        owningClass = _owningClass    }

    fun findReservedSeatFor(st: Student): Int? {
        return studentSeats[st]
    }

    fun reserveSeat(seatIdx: Int, student: Student) {
        studentSeats[student] = seatIdx
    }

    fun getStudentSeats(): Map<Student, Int> = studentSeats.toMap()

    fun tryReserveSeat(seatIdx: Int, student: Student, force: Boolean = false): Boolean {

        studentSeats[student]?.let {
            it == seatIdx && return true
            !force && return false
        }
        studentSeats[student] = seatIdx
        return true
    }
}