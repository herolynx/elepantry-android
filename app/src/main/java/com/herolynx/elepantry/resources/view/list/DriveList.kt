package com.herolynx.elepantry.resources.view.list

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.herolynx.elepantry.R
import com.herolynx.elepantry.core.log.debug
import com.herolynx.elepantry.core.log.error
import com.herolynx.elepantry.core.rx.observeOnUi
import com.herolynx.elepantry.core.rx.subscribeOnDefault
import com.herolynx.elepantry.core.ui.recyclerview.ListAdapter
import com.herolynx.elepantry.drive.CloudDrive
import com.herolynx.elepantry.drive.DriveType
import com.herolynx.elepantry.drive.Drives
import com.herolynx.elepantry.resources.core.model.ViewType

internal object DriveList {

    fun adapter(
            activity: Activity,
            onClickHandler: (DriveItemView) -> Unit,
            refreshClickHandler: (Boolean) -> Unit
    ):
            ListAdapter<CloudDrive, DriveItemView> =
            ListAdapter(
                    { ctx -> DriveItemView(ctx) },
                    { drive, h -> display(activity, drive!!, h.view, onClickHandler, refreshClickHandler) }
            )

    private fun display(
            activity: Activity,
            drive: CloudDrive,
            view: DriveItemView,
            onClickHandler: (DriveItemView) -> Unit,
            refreshClickHandler: (Boolean) -> Unit
    ) {
        view.setType(drive!!.type())
        val online = Drives.isOnline(activity, drive.type())
        view.setStatus(online)
        drive.refresh(refreshClickHandler).map { r -> view.setRefreshClickHandler { r() } }
        view.setOnClickListener {
            if (online) {
                onClickHandler(view)
            } else {
                Drives.login(activity, drive.type())
                        .subscribeOnDefault()
                        .observeOnUi()
                        .subscribe(
                                { drive -> display(activity, drive, view, onClickHandler, refreshClickHandler) },
                                { ex -> error("Couldn't login to drive - type: ${drive.type()}", ex) }
                        )
            }
        }
    }

}

internal class DriveItemView(ctx: Context) : LinearLayout(ctx) {

    init {
        LayoutInflater.from(context).inflate(R.layout.drive_list_item, this)
    }

    private val name = findViewById(R.id.drive_name) as TextView
    private val statusIcon = findViewById(R.id.drive_status_icon) as TextView
    private val statusDesc = findViewById(R.id.drive_status_desc) as TextView
    private val refresh = findViewById(R.id.drive_refresh) as TextView
    private val color = findViewById(R.id.drive_color)
    private var viewType: ViewType = ViewType.GOOGLE

    fun asView() = com.herolynx.elepantry.resources.core.model.View(name = name.text.toString(), type = viewType)

    internal fun setStatus(available: Boolean) {
        debug("[DriveListItem] Marking drive $viewType as available: $available")
        if (available) {
            statusIcon.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_menu_online, 0)
            statusDesc.setTextColor(Color.parseColor("#00d256"));
            statusDesc.text = context.getString(R.string.online)
        } else {
            statusIcon.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_menu_offline, 0)
            statusDesc.setTextColor(Color.parseColor("#d21400"));
            statusDesc.text = context.getString(R.string.offline)
        }
    }

    internal fun setRefreshClickHandler(h: () -> Unit) {
        refresh.visibility = View.VISIBLE
        refresh.setOnClickListener { h() }
    }

    internal fun setType(type: DriveType) {
        refresh.visibility = View.INVISIBLE
        when (type) {
            DriveType.GOOGLE_DRIVE -> {
                name.text = context.getText(R.string.google_drive)
                color.setBackgroundColor(Color.parseColor("#ea4235"));
                viewType = ViewType.GOOGLE
            }

            DriveType.DROP_BOX -> {
                name.text = context.getText(R.string.dropbox_drive)
                color.setBackgroundColor(Color.parseColor("#007ee6"));
                viewType = ViewType.DROP_BOX
            }

            else -> throw  UnsupportedOperationException("Unknown drive type: $type")
        }
    }

}

