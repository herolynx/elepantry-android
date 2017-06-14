package com.herolynx.elepantry.resources.core.model

import java.util.*

typealias Id = String

typealias Tag = String

internal fun newId(): Id = UUID.randomUUID().toString()
