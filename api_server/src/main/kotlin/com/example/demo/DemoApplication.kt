package com.example.demo

import com.example.demo.api.SchoolApiController
import com.example.demo.api.dto.*
//import com.example.demo.model.Gender
import com.example.demo.model.*
import com.example.demo.util.andPrint
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}


@EnableJpaRepositories("com.example.demo")
@SpringBootApplication
@ComponentScan( )
class DemoApplication : CommandLineRunner {
	override fun run(vararg args: String?) {	}
}


@Bean
fun corsFilter(): CorsFilter?
{
	val source = UrlBasedCorsConfigurationSource()
	val config = CorsConfiguration()
	config.allowCredentials = true
	config.addAllowedOrigin("*") // this allows all origin
	config.addAllowedHeader("*") // this allows all headers
	config.addAllowedMethod("OPTIONS")
	config.addAllowedMethod("HEAD")
	config.addAllowedMethod("GET")
	config.addAllowedMethod("PUT")
	config.addAllowedMethod("POST")
	config.addAllowedMethod("DELETE")
	config.addAllowedMethod("PATCH")
	source.registerCorsConfiguration("/**", config)
	return CorsFilter(source)
}



