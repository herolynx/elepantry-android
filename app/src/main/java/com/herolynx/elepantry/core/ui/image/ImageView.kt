package com.herolynx.elepantry.core.ui.image

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.download
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import org.funktionale.option.toOption

internal object ImageViewUtils {

    fun download(img: ImageView, uri: Uri) {
        img.visibility = View.INVISIBLE
        uri
                .download()
                .schedule()
                .observe()
                .subscribe(
                        { bitmap ->
                            img.visibility = View.VISIBLE
                            img.setImageBitmap(bitmap)
                        },
                        { ex ->
                            debug("[ImageView] Couldn't get image", ex)
                        }
                )
    }

}

fun ImageView.download(uri: Uri) {
    ImageViewUtils.download(this, uri)
}

fun ImageView.download(vararg urls: String?) = urls
        .filter { url -> url != null }
        .get(0)
        .toOption()
        .map { url -> Uri.parse(url) }
        .forEach { uri -> download(uri) }