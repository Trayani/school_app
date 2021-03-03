package com.example.demo.api

//import com.example.demo.ifTrue

import com.example.demo.api.dto.*
import com.example.demo.model.Subject
import com.example.demo.repo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional


@RestController
@CrossOrigin(origins = ["http://localhost:8080"])

class SchoolApiController {

    @ResponseBody
    @PostMapping("/new-classroom")
    fun newClassroom(@RequestBody dto: ClassroomDto, auth : Authentication) : ResponseEntity<CreatedEntityReponse>  {
        println("Auth name: " + auth.name)
        val ent = dbCtxt.classroomRepo.saveNew(dto)
        return createdRespEntity(ent)
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/new-teacher")
    fun newTeacher(@RequestBody dto: TeacherDto, auth : Authentication) : ResponseEntity<CreatedEntityReponse>   {
        val ent = dbCtxt.teacherRepo.saveNew(dto)
        return createdRespEntity(ent)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/new-class")
    fun newStudyClass(@RequestBody dto: StudyClassDto, auth : Authentication) : ResponseEntity<CreatedEntityReponse>  {
        val ent = dbCtxt.studyClassRepo.saveNew(dto)
//        ent.classTeacher?.let {  dbCtxt.teacherRepo.save(it) }
//        ent.classroom?.let {  dbCtxt.classroomRepo.save(it) }
//        ent.getStudents().forEach { dbCtxt.studentRepo.save(it) }
        return createdRespEntity(ent)
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/new-subject")
    fun newSubject(@RequestBody dto: SubjectDto, auth : Authentication) : ResponseEntity<CreatedEntityReponse>  {
        val ent = dbCtxt.subjectRepo.saveNew(dto)
//        ent.getTeachers().forEach { dbCtxt.teacherRepo.save(it) }
        return createdRespEntity(ent)
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/new-student")
    fun newStudent(@RequestBody dto: StudentDto, auth : Authentication) : ResponseEntity<CreatedEntityReponse>   {
        val ent = dbCtxt.studentRepo.saveNew(dto)
        return createdRespEntity(ent)
    }

    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/submit-grade")
    fun submitGrade(@RequestParam (name="studentUid", required=true ) studentUid: String
                    ,@RequestParam (name="subjectUid", required=true ) subjectUid: String
                    ,@RequestBody gradeDto: ExamGradeDto, auth : Authentication): String {
        val student = dbCtxt.findStudent(studentUid)
        val subj = dbCtxt.findSubject(subjectUid)
        student.addGrade(subj, gradeDto.toRawEnt())
        dbCtxt.studentRepo.save(student)
        return "Subject ${subj.name} grade submitted for student ${student.name} ${student.surname}"
    }

    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/reserve-seat")
    fun reserveRoomSeat(@RequestParam (name="studentUid", required=true ) studentUid: String
                        , @RequestParam (name="seatIdx", required=true ) seatIdx: Int
                        , @RequestParam (name="roomUid", required=true ) roomUid: String
                        , auth : Authentication) : String {
        val room = dbCtxt.findClassroom(roomUid)
        val student = dbCtxt.findStudent(studentUid)
        room.reserveSeat(seatIdx, student)
        dbCtxt.classroomRepo.save(room)
        return "Seat $seatIdx reserved in room ${room.name} for student ${student.name} ${student.surname}"
    }



    @GetMapping("/classroom")
    fun classroom(@RequestParam (name="uid", required=true ) uid:String, auth : Authentication ): ClassroomDto {
        return dbCtxt.findClassroom (uid).toDto()
    }

    @GetMapping("/teacher")
    fun teacher(@RequestParam (name="uid", required=true ) uid:String, auth : Authentication ): TeacherDto {
        return dbCtxt.findTeacher(uid).toDto()
    }

    @GetMapping("/student")
    fun student(@RequestParam (name="uid", required=true ) uid:String, auth : Authentication ): StudentDto {
        return dbCtxt.findStudent(uid).toDto()
    }

    @GetMapping("/class")
    fun studyClasses(@RequestParam (name="uid", required=true ) uid:String, auth : Authentication ):StudyClassDto {
        return dbCtxt.findStudyClass(uid).toDto()
    }

    @GetMapping("/subject")
    fun subject(@RequestParam (name="uid", required=true ) uid:String, auth : Authentication ):SubjectDto {
        return dbCtxt.findSubject  (uid).toDto()
    }



    @GetMapping("/classrooms")
    fun classrooms(): List<ClassroomDto> {
        return dbCtxt.classroomRepo.findAll().map { it.toDto() }
    }

    @GetMapping("/teachers")
    fun teachers(): List<TeacherDto> {
        return dbCtxt.teacherRepo.findAll().map { it.toDto() }
    }

    @GetMapping("/students")
    fun students() : List<StudentDto> {
        return dbCtxt.studentRepo.findAll().map { it.toDto() }
    }

    @GetMapping("/classes")
    fun studyClasses():List<StudyClassDto> {
        return dbCtxt.studyClassRepo.findAll().map { it.toDto() }
    }

    @GetMapping("/subjects")
    fun subjects():List<SubjectDto> {
        return dbCtxt.subjectRepo.findAll().map { it.toDto() }
    }


}


