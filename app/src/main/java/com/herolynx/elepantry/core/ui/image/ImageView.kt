package com.herolynx.elepantry.core.ui.image

import android.view.View
import android.widget.ImageView
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.net.download
import com.herolynx.elepantry.core.rx.observeOnDefault
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import org.funktionale.option.Option
import rx.Observable
import rx.Subscription
import java.io.InputStream

internal object ImageViewUtils {

    fun <T> download(
            img: ImageView,
            inStream: Observable<InputStream>,
            parentId: Option<T> = Option.None,
            parentIdGetter: () -> Option<T> = { Option.None }
    ): Subscription {
        img.visibility = View.INVISIBLE
        return inStream
                .flatMap { s -> s.download() }
                .subscribeOnDefault()
                .observeOnDefault()
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
        inStream: Observable<InputStream>,
        parentId: Option<T> = Option.None,
        parentIdGetter: () -> Option<T> = { Option.None }
): Subscription = ImageViewUtils.download(this, inStream, parentId, parentIdGetter)
