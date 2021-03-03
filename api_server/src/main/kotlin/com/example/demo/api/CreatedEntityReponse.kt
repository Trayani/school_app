package com.example.demo.api

import com.example.demo.model.AbstractEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CreatedEntityReponse (ent : AbstractEntity) {
    val newUid = ent.uid
    val message = "New " + ent.javaClass.simpleName + " record created with UID: " + ent.uid
}

fun createdRespEntity(ent: AbstractEntity) = ResponseEntity(CreatedEntityReponse(ent), HttpStatus.CREATED)