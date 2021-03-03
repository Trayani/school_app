package com.example.authserver.auth


//import com.opencodez.getUserRepository
//import com.opencodez.utils.ifNotNull
//import com.opencodez.utils.tryOrPrint
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

//@Transactional
@Service
class UserDetailsService (val userRepository : UserRepository)  : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        print("\nloading user: $username")


        userRepository.findByUserName(username.toUpperCase())?.let {
            print("\nloading pw: ${it.password} ")
            val user = User.withUsername(it.userName).password(it.password).roles(it.role).build()
            print("\nOK  ${it.password} ")
            return user
        }

        print("user: $username not found")
        throw UsernameNotFoundException("user: $username not found")
       }
}