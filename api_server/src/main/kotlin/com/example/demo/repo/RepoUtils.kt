package com.example.demo.repo

import com.example.demo.api.AbstractDto
import com.example.demo.model.AbstractEntity
import org.apache.commons.lang3.RandomStringUtils
import javax.persistence.EntityExistsException
import javax.persistence.EntityNotFoundException

fun <T: AbstractEntity> SchoolUidRepository<T>.findByUidOrThrow(uid: String ): T {
    findFirstByUid(uid)?.let { return it }
    throw EntityNotFoundException()
}

inline fun <reified T: AbstractEntity, reified R : SchoolUidRepository<T>> R.saveNew(dto: AbstractDto<T>): T {
    synchronized(this) {
        validateNewUid(dto)
        return save(dto.toEnt())
    }
}

fun <T: AbstractEntity, R : SchoolUidRepository<T>> R.validateNewUid(dto: AbstractDto<T>) {
    val newUid = dto.uid?.toUpperCase()
    if (newUid.isNullOrBlank()) {
        dto.uid = getUniqueUid()
        return
    }

    if ( newUid.length != UID_LEN)
        throw IllegalArgumentException("Entity UID has to be exactly $UID_LEN characters long " +
                "(uid '$newUid' has ${newUid.length})")
    if (countByUid(newUid) > 0 )
        throw EntityExistsException("There already exists an entity with the UID: $newUid")
}

fun <T: AbstractEntity, R : SchoolUidRepository<T>> R.getUniqueUid(): String {
    val strUid = RandomStringUtils.randomAlphabetic(UID_CHAR_LEN).toUpperCase()
    val count = countByUidStartsWith(strUid)
    return   strUid  +   String.format("%0${UID_DEC_LEN}d" , count)// getUniqueUid()
}