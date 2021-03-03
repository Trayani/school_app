package com.example.demo.repo

import org.hibernate.SessionFactory
import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration
import org.modelmapper.convention.MatchingStrategies
import org.modelmapper.spi.MatchingStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service


private var dbContextInstance: DbContext? = null

val dbCtxt: DbContext
    get() = dbContextInstance!!


@Component
class DbContext(
        @Autowired val classroomRepo: ClassroomRepo,
        @Autowired val studentRepo: StudentRepo,
        @Autowired val teacherRepo: TeacherRepo,
        @Autowired val subjectRepo: SubjectRepo,
        @Autowired val studyClassRepo: StudyClassRepo,
        @Autowired val sessionFactory: SessionFactory
) {
    val modelMapper = ModelMapper()

    @Bean
    fun init() {
        dbContextInstance = this
        modelMapper.configuration.setFieldMatchingEnabled(true)
        modelMapper.configuration.isAmbiguityIgnored = true
        modelMapper.configuration.matchingStrategy = MatchingStrategies.LOOSE
        modelMapper.validate()
    }


    fun findSubject(uid: String) = subjectRepo.findByUidOrThrow(uid)
    fun findStudent(uid: String) = studentRepo.findByUidOrThrow(uid)
    fun findTeacher(uid: String) = teacherRepo.findByUidOrThrow(uid)
    fun findClassroom(uid: String) = classroomRepo.findByUidOrThrow(uid)
    fun findStudyClass(uid: String) = studyClassRepo.findByUidOrThrow(uid)
}
