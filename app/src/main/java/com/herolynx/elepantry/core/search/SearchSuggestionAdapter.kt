package com.herolynx.elepantry.core.search

import android.content.Context
import android.database.AbstractCursor
import android.database.Cursor
import android.support.v4.widget.SimpleCursorAdapter

class SearchSuggestionAdapter(ctx: Context) : SimpleCursorAdapter(ctx, android.R.layout.simple_list_item_1, null, FIELDS_VISIBLE, VIEW_IDS, 0) {

    override fun runQueryOnBackgroundThread(constraint: CharSequence?): Cursor {
        return SearchSuggestionCursor(constraint.toString())
    }

    companion object {

        internal val FIELD_COLUMN_NAMES = arrayOf("_id", "result")
        private val FIELDS_VISIBLE = arrayOf("result")
        private val VIEW_IDS = intArrayOf(android.R.id.text1)

    }


}

class SearchSuggestionCursor(private val text: String) : AbstractCursor() {

    private val results: List<String> = listOf("aaa", "bbb", "ccc")

    override fun getString(idx: Int): String = when (idx) {

        1 -> results.get(position)

        else -> throw IllegalArgumentException("getString: Only one column supported with index 1 - got: $idx")

    }

    override fun getLong(idx: Int): Long = when (idx) {

        0 -> position.toLong()

        else -> throw IllegalArgumentException("getLong: Only one column supported with index 0 - got: $idx")

    }

    override fun getCount(): Int = results.size

    override fun isNull(idx: Int): Boolean = false

    override fun getColumnNames(): Array<String> = SearchSuggestionAdapter.FIELD_COLUMN_NAMES

    override fun getShort(p0: Int): Short = throw UnsupportedOperationException("getShort not supported")

    override fun getFloat(p0: Int): Float = throw UnsupportedOperationException("getFloat not supported")

    override fun getDouble(p0: Int): Double = throw UnsupportedOperationException("getDouble not supported")

    override fun getInt(p0: Int): Int = throw UnsupportedOperationException("getInt not supported")


}