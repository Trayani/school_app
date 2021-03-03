package com.example.authserver

import com.example.authserver.auth.ApiUser
import com.example.authserver.auth.ApiUserDto
import com.example.authserver.auth.UserAuth
import com.example.authserver.auth.UserRepository
//import com.example.authserver.auth.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

@Component
@RestController
class RegistrationController  ( val passwordEncoder: PasswordEncoder, val userRepo : UserRepository) {

    @PostMapping("/register-teacher")
    @ResponseStatus(code = HttpStatus.CREATED)
    fun teacherUser(@RequestBody user : ApiUserDto) {
        val user = ApiUser(user.userName!!.toUpperCase() ,
                passwordEncoder.encode(user.password!!)
                , UserAuth.TEACHER)
        userRepo.save(user)

        println("User (teacher) ${user.userName!!.toUpperCase()} registered")
    }


    @PostMapping("/register-student")
    fun studentUser(@RequestBody user : ApiUserDto) {
        val user = ApiUser(user.userName ,user.password, UserAuth.STUDENT)
        userRepo.save(user)
        println("User (student) ${user.userName!!.toUpperCase()} registered")
    }

}