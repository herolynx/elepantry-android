package com.herolynx.elepantry.ext.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.warn
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import org.funktionale.tries.Try
import rx.Observable
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object Storage {

    private fun download(a: Activity, fileName: String, download: (OutputStream) -> Long): Observable<File> = Observable.defer {
        Storage.downloadDirectory(a)
                .map { downloadDir ->
                    val file = File(downloadDir, fileName)
                    if (file.exists()) {
                        Observable.just(file)
                    } else {
                        Observable.defer {
                            val bytes = FileOutputStream(file).use { outputStream -> download(outputStream) }
                            debug("[Storage][Download] Download status - file: $file, bytes: $bytes")
                            Observable.just(file)
                        }
                    }
                }
                .rescue { ex -> Try.Success(Observable.error(ex)) }
                .get()
                .map { file ->
                    Storage.notifyAboutNewFile(a, file)
                    file
                }
    }

    fun downloadAndOpen(activity: Activity, fileName: String, download: (OutputStream) -> Long, beforeAction: () -> Unit, afterAction: () -> Unit) {
        beforeAction()
        download(activity, fileName, download)
                .subscribeOnDefault()
                .observeOn(Schedulers.io())
                .subscribe(
                        { f ->
                            activity.runOnUiThread {
                                afterAction()
                                Storage.viewFileInExternalApp(activity, f)
                            }
                        },
                        { ex ->
                            warn("[Storage][File] Download & open error - $fileName", ex)
                            afterAction()
                        }
                )
    }

    fun viewFileInExternalApp(a: Activity, f: File): Try<Boolean> = Try {
        val intent = Intent(Intent.ACTION_VIEW)
        val mime = MimeTypeMap.getSingleton()
        val ext = f.name.substring(f.name.indexOf(".") + 1)
        val type = mime.getMimeTypeFromExtension(ext)

        intent.setDataAndType(Uri.fromFile(f), type)

        // Check for a handler first to avoid a crash
        val manager = a.getPackageManager()
        val resolveInfo = manager.queryIntentActivities(intent, 0)
        if (resolveInfo.size > 0) {
            a.startActivity(intent)
            true
        } else {
            false
        }
    }
            .onFailure { ex -> warn("[Storage] Couldn't display file in external app - ${f.absolutePath}", ex) }

    fun notifyAboutNewFile(a: Activity, f: File) {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(f)
        a.sendBroadcast(intent)
    }

    fun downloadDirectory(a: Activity): Try<File> = Try {
        Permissions.assertExternalStoragePermission(a)
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists() && !downloadDir.mkdirs()) {
            throw RuntimeException("Unable to create directory: $downloadDir")
        } else if (!downloadDir.isDirectory) {
            throw IllegalStateException("Download path is not a directory: $downloadDir")
        }
        downloadDir
    }
            .onFailure { ex -> warn("[Storage] Getting download directory error", ex) }

}