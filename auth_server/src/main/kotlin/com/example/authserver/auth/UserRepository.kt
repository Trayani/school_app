package com.example.authserver.auth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<ApiUser, Long> {
    fun findByUserName(uid:String) : ApiUser?
}