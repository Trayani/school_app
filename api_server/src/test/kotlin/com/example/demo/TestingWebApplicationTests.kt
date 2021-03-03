package com.example.demo

import com.example.demo.api.AbstractDto
import com.example.demo.api.SchoolApiController
import com.example.demo.api.dto.*
import com.example.demo.model.Gender
import com.example.demo.model.StudentStatus
import com.example.demo.repo.SchoolUidRepository
import com.example.demo.repo.dbCtxt
import com.example.demo.util.notNull
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.json.JacksonJsonParser
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional


@SpringBootTest(classes = [DemoApplication::class])
@AutoConfigureMockMvc
class TestingWebApplicationTests {

    @Autowired
    var controller: SchoolApiController? = null

    @Autowired
    private val mvc: MockMvc? = null

    val mapper = ObjectMapper().also { it.setSerializationInclusion(JsonInclude.Include.NON_NULL) }

    @Test
    fun contextLoads() {
        Assert.assertNotNull(controller)
        Assert.assertNotNull(dbCtxt)
        Assert.assertNotNull(mvc)
    }


    @Transactional
    @Test
    fun testREST() {

        registerUser("brx92","dhg4fg32",
                 "http://localhost:8082/register-teacher")

        val token = "Bearer " +  obtainAccessToken(
                "brx92","dhg4fg32",
                "exampleClient","exampleSecret",
                "http://localhost:8082/oauth/token")

        val clDto = sampleClassroomDto()
        testNewEntityPersistance(clDto, "/new-classroom", "/classroom", dbCtxt.classroomRepo, token)

        val teacherDto = sampleTeacherDto()
        testNewEntityPersistance(teacherDto, "/new-teacher", "/teacher", dbCtxt.teacherRepo, token)

        val stClassDto = sampleStudyClassDto()
        testNewEntityPersistance(stClassDto, "/new-class", "/class", dbCtxt.studyClassRepo, token)

        val subjDto = sampleSubjectDto(teacherDto.uid)
        testNewEntityPersistance(subjDto, "/new-subject", "/subject", dbCtxt.subjectRepo, token)

        val stuDto = sampleStudentDto(stClassDto.uid)
        testNewEntityPersistance(stuDto, "/new-student", "/student", dbCtxt.studentRepo, token)


        // test exam grade submission
        val gradeInt = 2
        val gradeDto = sampleExamGradeDto(teacherDto.uid!!, gradeInt)
        mvc!!.perform(
                post("/submit-grade").header("Authorization",  token)
                        .param("studentUid", stuDto.uid!!)
                        .param("subjectUid", subjDto.uid!!)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(gradeDto))
        ).andExpect(status().isAccepted)

        val subjectEnt = dbCtxt.findSubject(subjDto.uid!!)
        val studentEnt = dbCtxt.findStudent(stuDto.uid!!)
        val grade = studentEnt.getCurrentGrades()[subjectEnt]!!.grades[0]
        assert(grade.grade == gradeInt)

        val reservedSeat = 5
        mvc.perform(
                post("/reserve-seat").header("Authorization",  token)
                        .param("studentUid", stuDto.uid!!)
                        .param("seatIdx", reservedSeat.toString())
                        .param("roomUid", clDto.uid!!)
        ).andExpect(status().isAccepted)


        val classroomEnt = dbCtxt.findClassroom(clDto.uid!!)
        assert((classroomEnt.findReservedSeatFor(studentEnt)) == reservedSeat)
    }

    @Transactional
    @Test
    fun testRelations() {
        val auth =  UsernamePasswordAuthenticationToken("CV291", "dhg4fg32")

        val subjectDto = sampleSubjectDto()
        controller!!.newSubject(subjectDto , auth )
        val teacherDto = sampleTeacherDto()
        teacherDto.subjectUid = mutableSetOf(subjectDto.uid!!)
        controller!!.newTeacher(teacherDto, auth)

        run {
            val subjectEnt = dbCtxt.findSubject(subjectDto.uid!!)
            assert(subjectEnt.getTeachers().any { it.uid == teacherDto.uid!! })
        }


        val classroomDto = sampleClassroomDto()
        controller!!.newClassroom(classroomDto, auth)
        val studyClassDto = sampleStudyClassDto(teacherDto.uid, classroomDto.uid)
        controller!!.newStudyClass(studyClassDto, auth)

        run {
            val teacherEnt = dbCtxt.findTeacher(teacherDto.uid!!)
            assert(teacherEnt.studyClass?.uid == studyClassDto.uid!!)

            val roomEnt = dbCtxt.findClassroom(classroomDto.uid!!)
            assert(roomEnt.owningClass?.uid == studyClassDto.uid!!)
        }


        val studentDto = sampleStudentDto(studyClassDto.uid!!)
        controller!!.newStudent(studentDto, auth)

        run {
            val studyClass = dbCtxt.findStudyClass(studyClassDto.uid!!)
            assert(studyClass.getStudents().any { it.uid!! == studentDto.uid!! })
        }
//        Thread.sleep(454545454545454L)
    }

    fun testNewEntityPersistance(ent: AbstractDto<*>, postUrl: String, getUrl: String, repo: SchoolUidRepository<*>, token: String) {

        val POSTResponse = mvc!!.perform(
                post(postUrl).header("Authorization",  token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(ent))
        ).andExpect(status().isCreated).andReturn().response

        val jsonMap = mapper.readValue(POSTResponse.contentAsString, HashMap::class.java)
        val newUid = jsonMap["newUid"] as String
        ent.uid = newUid
        val newEntity = repo.findFirstByUid(newUid)
        assert(newEntity.notNull)

        val GETResponse = mvc.perform(
                get(getUrl).accept(MediaType.APPLICATION_JSON)
                        .param("uid", newUid)
                        .header("Authorization",  token)
        ).andExpect(status().isOk).andReturn().response
        val respDto = mapper.readValue(GETResponse.contentAsString, ent.javaClass)
        assert(respDto.notNull)
    }

    private fun registerUser(username: String,
                                  pw: String,
//                                  clientName: String,
//                                  clientPw: String,
                                  url: String) {

        val body = "{ \"userName\":\"$username\", \"password\":\"$pw\" }"
        val registrationResp = RestAssured.given()  //.auth().basic(clientName, clientPw)
                .header("Content-Type", "application/json")
                .body( body)
                .post(url).andReturn()

        val k =3
    }

    private fun obtainAccessToken(username: String,
                                  pw: String,
                                  clientName: String,
                                  clientPw: String,
                                  tokenUrl: String): String {



        val resp = RestAssured.given().auth()
                .basic(clientName, clientPw)
                .params(mutableMapOf("grant_type" to "password", "username" to username, "password" to pw))
                .post(tokenUrl)

        val json = resp.prettyPrint()
        return  JacksonJsonParser().parseMap(json) ["access_token"] as String
    }
}

fun sampleClassroomDto() = ClassroomDto().apply {
    name = "SUB0"
    capacity = 30
}


fun sampleTeacherDto() = TeacherDto().apply {
    gender = Gender.WOMAN
    name = "Maud"
    surname = "Nineray"
}

fun sampleStudyClassDto(teacherUid: String? = null, roomUid: String? = null) = StudyClassDto().apply {
    classTeacherUid = teacherUid
    classroomUid = roomUid
    name = "A"
    yearOfStudy = 2
}

fun sampleExamGradeDto(teacherUid: String, grad: Int = 5) = ExamGradeDto().apply {
    grade = grad
    this.teacherUid = teacherUid
    submittedTs = Timestamp(System.currentTimeMillis())
}

fun sampleSubjectDto(teachUid: String? = null) = SubjectDto().apply {
    name = "Math"
    optional = true

    yearsOfStudy = mutableSetOf(1, 3, 4)
    teachUid?.let { teacherUid = mutableSetOf(it) }

}

fun sampleStudentDto(classUid: String?) = StudentDto().apply {
    birthDate = Date(System.currentTimeMillis())
    gender = Gender.WOMAN
    name = "Jogn"
    surname = "Houd"
    status = StudentStatus.ENROLLED
    studyClassUid = classUid
    joinedDate = Date(System.currentTimeMillis())
}
