package com.herolynx.elepantry.core.ui.image

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.download
import com.herolynx.elepantry.core.rx.observe
import com.herolynx.elepantry.core.rx.schedule
import org.funktionale.option.Option
import org.funktionale.option.toOption
import rx.Subscription

internal object ImageViewUtils {

    fun <T> download(
            img: ImageView,
            uri: Uri,
            parentId: Option<T> = Option.None,
            parentIdGetter: () -> Option<T> = { Option.None }
    ): Subscription {
        img.visibility = View.INVISIBLE
        return uri
                .download()
                .schedule()
                .observe()
                .subscribe(
                        { bitmap ->
                            if (parentId.equals(parentIdGetter())) {
                                img.visibility = View.VISIBLE
                                img.setImageBitmap(bitmap)
                            }
                        },
                        { ex ->
                            debug("[ImageView] Couldn't get image", ex)
                        }
                )
    }

}

fun <T> ImageView.download(
        uri: Uri,
        parentId: Option<T> = Option.None,
        parentIdGetter: () -> Option<T> = { Option.None }
): Subscription = ImageViewUtils.download(this, uri, parentId, parentIdGetter)

fun <T> ImageView.download(
        parentId: Option<T> = Option.None,
        parentIdGetter: () -> Option<T> = { Option.None },
        vararg urls: String?
): Option<Subscription> = urls
        .filter { url -> url != null }
        .get(0)
        .toOption()
        .map { url -> Uri.parse(url) }
        .map { uri -> download(uri, parentId, parentIdGetter) }