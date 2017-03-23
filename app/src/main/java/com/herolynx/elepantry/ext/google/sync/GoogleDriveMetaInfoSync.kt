package com.herolynx.elepantry.ext.google.sync

import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.ext.google.drive.GoogleDrivePage
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.option.Option
import org.funktionale.tries.Try

class GoogleDriveMetaInfoSync {

    fun sync(page: Try<GoogleDrivePage>, res: Repository<Resource>) {
        page.map { p ->
            p.resources()
                    .filter { e -> !e.deleted }
                    .map { e -> e.data }
                    .subscribe(
                            { r ->
                                res.find(r.id)
                                        .getOrElse { Option.None }
                                        .filter { f -> !f.isTheSame(r) }
                                        .map { f -> res.save(r.merge(f)) }
                            },
                            { ex -> error("Error while syncing Google Drive meta info", ex) },
                            { if (p.hasNext()) sync(p.next(), res) }
                    )
        }
    }

}