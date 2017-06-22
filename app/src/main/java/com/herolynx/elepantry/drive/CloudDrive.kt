package com.herolynx.elepantry.drive

import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourceView
import org.funktionale.tries.Try

interface CloudDrive {

    fun driveView(): ResourceView

    fun type(): DriveType

    fun cloudResource(r: Resource): Try<CloudResource>

}