package com.example.demo.api

import com.example.demo.util.asSet
import com.example.demo.repo.dbCtxt
import com.example.demo.model.*



abstract class AbstractDto< T: AbstractEntity> {
    var id: Long? = null
    var uid : String? = null

    fun toEnt():T = toRawEnt().also { it.uid = uid  }
    abstract fun toRawEnt():T
    abstract fun mapFromEntity(ent: T)
}


inline fun < reified T: AbstractEntity, reified T2 : AbstractDto<T>> T.toDtoUnchecked() :  T2 {
    val dto = dbCtxt.modelMapper.map(this, T2::class.java)
    dto.mapFromEntity(this )
    return dto
}

fun <T:AbstractEntity> Iterable<T>.toUidSet () = this.map { it.uid!! }.asSet()

