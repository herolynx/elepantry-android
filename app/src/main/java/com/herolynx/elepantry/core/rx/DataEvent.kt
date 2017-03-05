package com.herolynx.elepantry.core.rx

/**
 * Event informs about change made in data
 */
data class DataEvent<T>(val data: T, val deleted: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        val e = other as? DataEvent<*>
        return e?.data?.equals(data) ?: false
    }

    override fun hashCode(): Int {
        return data?.hashCode() ?: super.hashCode()
    }

}