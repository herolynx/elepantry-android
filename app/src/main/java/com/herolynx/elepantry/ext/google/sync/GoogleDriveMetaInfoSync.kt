package com.herolynx.elepantry.ext.google.sync


import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.log.info
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import com.herolynx.elepantry.ext.google.drive.GoogleDriveView
import com.herolynx.elepantry.repository.Repository
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.resources.core.service.ResourcePage
import org.funktionale.tries.Try
import rx.schedulers.Schedulers

class GoogleDriveMetaInfoSync(
        private val gDrive: GoogleDriveView,
        private val resRep: Repository<Resource>
) {

    fun sync(jobStatus: (Boolean) -> Unit) {
        Thread {
            jobStatus(true)
            sync(gDrive.search(), resRep, { jobStatus(false) })
        }.start()
    }

    private fun sync(page: Try<out ResourcePage>, res: Repository<Resource>, afterLogic: () -> Unit) {
        page.map { p ->
            p.resources()
                    .filter { e -> !e.deleted }
                    .map { e -> e.data }
                    .subscribe(
                            { r ->
                                res.find(r.id)
                                        .filter { r -> r.isDefined() }
                                        .map { r -> r.get() }
                                        .filter { f -> !f.isTheSame(r) }
                                        .subscribeOnDefault()
                                        .observeOn(Schedulers.io())
                                        .subscribe(
                                                { f ->
                                                    val newVersion = r.merge(f)
                                                    debug("$TAG Saving changed resource -  new: $newVersion, old: $f")
                                                    res.save(newVersion)
                                                            .subscribeOnDefault()
                                                            .observeOn(Schedulers.io())
                                                            .subscribe(
                                                                    { _ -> debug("$TAG New version of resource saved: $newVersion") },
                                                                    { ex -> error("$TAG New version of resource not saved: $newVersion", ex) }
                                                            )
                                                },
                                                { ex -> error("$TAG Error while getting result for resource: $r", ex) }
                                        )
                            },
                            { ex -> error("$TAG Error while syncing Google Drive meta info", ex) },
                            {
                                if (p.hasNext())
                                    sync(p.next(), res, afterLogic)
                                else {
                                    info("$TAG Sync completed")
                                    afterLogic()
                                }
                            }
                    )
        }
    }

    companion object {

        val TAG = "[GoogleDrive][Sync]"

        fun create(
                gDrive: GoogleDriveView,
                resRep: Repository<Resource> = Config.repository.userResources()
        ): GoogleDriveMetaInfoSync = GoogleDriveMetaInfoSync(
                gDrive,
                resRep
        )

    }

}