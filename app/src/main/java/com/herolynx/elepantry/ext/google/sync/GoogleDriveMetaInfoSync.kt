package com.herolynx.elepantry.ext.google.sync


import android.app.Activity
import com.herolynx.elepantry.config.Config
import com.herolynx.elepantry.core.generic.toISO8601
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.log.info
import com.herolynx.elepantry.core.repository.Repository
import com.herolynx.elepantry.core.rx.schedule
import com.herolynx.elepantry.ext.google.drive.GoogleDrivePage
import com.herolynx.elepantry.ext.google.drive.GoogleDriveView
import com.herolynx.elepantry.resources.core.model.Resource
import com.herolynx.elepantry.user.model.UserMetaInf
import com.herolynx.elepantry.user.model.getLastSyncTimeDate
import org.funktionale.tries.Try
import org.joda.time.Duration
import rx.Subscription
import rx.schedulers.Schedulers
import java.util.*

class GoogleDriveMetaInfoSync(
        private val gDrive: GoogleDriveView,
        private val resRep: Repository<Resource>,
        private val userRep: Repository<UserMetaInf>,
        private val syncEvery: Duration,
        private val jobId: String = "googleSyncInfo"
) {

    private var subscription: Subscription? = null
    private var syncOnGoing = false

    fun isSyncing() = syncOnGoing

    fun sync(scheduleInMs: Long = 5000, progressBar: (Boolean) -> Unit, afterSyncLogic: () -> Unit = {}) {
        if (syncOnGoing) {
            return
        }
        Thread({
            Thread.sleep(scheduleInMs)
            debug("$TAG Checking sync status - on-going: $syncOnGoing")
            subscription = userRep.asObservable()
                    .filter { e -> !e.deleted && e.data.id.equals(jobId) }
                    .map { e -> e.data }
                    .firstOrDefault(UserMetaInf(jobId))
                    .schedule()
                    .observeOn(Schedulers.io())
                    .subscribe(
                            { u ->
                                debug("$TAG Checking last sync time - info: $u")
                                if (Duration(u.getLastSyncTimeDate().time, Date().time).isLongerThan(syncEvery)) {
                                    debug("$TAG Starting sync - info: $u")
                                    syncOnGoing = true
                                    progressBar(true)
                                    sync(gDrive.search(), resRep, u, {
                                        syncOnGoing = false
                                        subscription?.unsubscribe()
                                        progressBar(false)
                                        afterSyncLogic()
                                    })
                                }
                            },
                            { ex -> error("$TAG Sync error", ex) },
                            { subscription?.unsubscribe() }
                    )
        }).start()
    }

    private fun sync(page: Try<GoogleDrivePage>, res: Repository<Resource>, user: UserMetaInf, afterLogic: () -> Unit) {
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
                                        .schedule()
                                        .observeOn(Schedulers.io())
                                        .subscribe(
                                                { f ->
                                                    val newVersion = r.merge(f)
                                                    debug("$TAG Saving changed resource -  new: $newVersion, old: $f")
                                                    res.save(newVersion)
                                                            .schedule()
                                                            .observeOn(Schedulers.io())
                                                            .subscribe(
                                                                    { info -> debug("$TAG New version of resource saved: $newVersion") },
                                                                    { ex -> error("$TAG New version of resource not saved: $newVersion", ex) }
                                                            )
                                                },
                                                { ex -> error("$TAG Error while getting result for resource: $r", ex) }
                                        )
                            },
                            { ex -> error("$TAG Error while syncing Google Drive meta info", ex) },
                            {
                                if (p.hasNext())
                                    sync(p.next(), res, user, afterLogic)
                                else {
                                    info("$TAG Sync completed")
                                    userRep
                                            .save(user.copy(lastSyncTime = Date().toISO8601().getOrElse { "" }))
                                            .schedule()
                                            .observeOn(Schedulers.io())
                                            .subscribe(
                                                    { info -> debug("$TAG Sync info saved: $info") },
                                                    { ex -> error("$TAG Sync info not saved", ex) }
                                            )
                                    afterLogic()
                                }
                            }
                    )
        }
    }

    companion object {

        val TAG = "[GoogleDrive][Sync]"

        fun create(
                a: Activity,
                gDrive: GoogleDriveView = GoogleDriveView.Factory.create(a).get(),
                resRep: Repository<Resource> = Config.repository.userResources(),
                userRep: Repository<UserMetaInf> = Config.repository.userMetaInfo(),
                syncEvery: Duration = Duration.standardHours(4)
        ): GoogleDriveMetaInfoSync = GoogleDriveMetaInfoSync(
                gDrive,
                resRep,
                userRep,
                syncEvery
        )

    }

}