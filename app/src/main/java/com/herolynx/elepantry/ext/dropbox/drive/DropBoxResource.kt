package com.herolynx.elepantry.ext.dropbox.drive

import android.app.Activity
import com.dropbox.core.android.DbxOfficialAppConnector
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.ThumbnailSize
import com.herolynx.elepantry.core.Result
import com.herolynx.elepantry.core.android.Storage
import com.herolynx.elepantry.core.func.Retry
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.observeOnDefault
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import com.herolynx.elepantry.drive.CloudResource
import com.herolynx.elepantry.ext.dropbox.auth.DropBoxSession
import com.herolynx.elepantry.resources.core.model.Resource
import org.funktionale.tries.Try
import rx.Observable
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DropBoxResource(
        private val metaInfo: Resource,
        private val client: DbxClientV2,
        private val session: DropBoxSession
) : CloudResource {

    private fun download(a: Activity): Observable<File> = Observable.defer {
        Storage.downloadDirectory(a)
                .map { downloadDir ->
                    val file = File(downloadDir, "${metaInfo.id}_${metaInfo.version}.${metaInfo.extension}")
                    if (!file.exists()) {
                        FileOutputStream(file)
                                .use { outputStream ->
                                    client.files()
                                            .download(metaInfo.downloadLink, metaInfo.version)
                                            .download(outputStream)
                                }
                    }
                    file
                }
                .map { file ->
                    Storage.notifyAboutNewFile(a, file)
                    Observable.just(file)
                }
                .onFailure { ex -> warn("[DropBox] Download error", ex) }
                .getOrElse { Observable.error(RuntimeException("Couldn't download file: $metaInfo")) }
    }

    private fun downloadAndOpen(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit) {
        beforeAction()
        download(activity)
                .subscribeOnDefault()
                .observeOnDefault()
                .subscribe(
                        { f ->
                            activity.runOnUiThread {
                                afterAction()
                                Storage.viewFileInExternalApp(activity, f)
                            }
                        },
                        { ex ->
                            warn("[DropBox][File] Preview error - $metaInfo", ex)
                            afterAction()
                        }
                )
    }

    override fun preview(activity: Activity, beforeAction: () -> Unit, afterAction: () -> Unit): Try<Result> = Try {
        val dropBox = DbxOfficialAppConnector(session.uid)
        val openIntent = dropBox.getPreviewFileIntent(activity, metaInfo.downloadLink, metaInfo.version)
        if (SUPPORTED_PREVIEW_FILE_EXT.contains(metaInfo.extension) && openIntent != null) {
            //start preview directly in DropBox app
            activity.startActivity(openIntent)
            Result(true)
        } else {
            //TODO remove when DropBox app can handle all contents
            downloadAndOpen(activity, beforeAction, afterAction)
            Result(true)
        }
    }

    override fun thumbnail(): rx.Observable<InputStream> = Observable.defer {
        if (!metaInfo.isImageType()) {
            Observable.empty()
        } else {
            Retry.executeWithRetries(logic = {
                Try {
                    Observable.just(client.files()
                            .getThumbnailBuilder(metaInfo.thumbnailLink)
                            .withSize(ThumbnailSize.W640H480)
                            .start()
                            .inputStream
                    )
                }
            })
                    .onFailure { ex -> warn("[DropBox][Thumbnail] Couldn't get image - resource: $metaInfo", ex) }
                    .getOrElse { Observable.empty() }
        }
    }

    override fun metaInfo(): Resource = metaInfo

    companion object {

        private val SUPPORTED_PREVIEW_FILE_EXT = setOf(
                "pdf", "ai", "doc", "docm", "docx", "eps", "odp", "odt",
                "pps", "ppsm", "ppsx", "ppt", "pptm", "pptx", "rtf",
                "csv", "ods", "xls", "xlsm", "xlsx"
        )

    }

}