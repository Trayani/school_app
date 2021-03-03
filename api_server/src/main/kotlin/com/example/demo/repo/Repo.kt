package com.example.demo.repo
import com.example.demo.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

// TODO: configurable UID length
val UID_CHAR_LEN = 3
val UID_DEC_LEN = 3
val UID_LEN = UID_CHAR_LEN + UID_DEC_LEN

@NoRepositoryBean
interface   SchoolUidRepository<T: AbstractEntity>  : JpaRepository<T, Long>   {
    fun countByUid(uid: String) : Long
    fun findFirstByUid(uid: String) : T?
    fun countByUidStartsWith(uid:String ) : Long

//    fun updateAndSave(ent: T)
}

@Repository
 interface  ClassroomRepo : SchoolUidRepository<Classroom> {

}

@Repository
 interface  StudentRepo : SchoolUidRepository<Student>

@Repository
 interface TeacherRepo : SchoolUidRepository<Teacher>

@Repository
 interface SubjectRepo : SchoolUidRepository<Subject>

@Repository
 interface StudyClassRepo : SchoolUidRepository<StudyClass> {
    fun findByYearOfStudy(year : Int) : List<StudyClass>
}

