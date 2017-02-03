package com.herolynx.elepantry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class ChooserActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chooser)

        // Set up ListView and Adapter
        val listView = findViewById(R.id.list_view) as ListView

        val adapter = MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES)
        adapter.setDescriptionIds(DESCRIPTION_IDS)

        listView.adapter = adapter
        listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val clicked = CLASSES[position]
        startActivity(Intent(this, clicked))
    }

    class MyArrayAdapter(private val mContext: Context, resource: Int, private val mClasses: Array<Class<*>>) : ArrayAdapter<Class<*>>(mContext, resource, mClasses) {
        private var mDescriptionIds: IntArray? = null

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view: View? = convertView;

            if (convertView == null) {
                val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(android.R.layout.simple_list_item_2, null)
            }

            (view?.findViewById(android.R.id.text1) as TextView).text = mClasses[position].simpleName
            (view?.findViewById(android.R.id.text2) as TextView).setText(mDescriptionIds!![position])

            return view as View
        }

        fun setDescriptionIds(descriptionIds: IntArray) {
            mDescriptionIds = descriptionIds
        }
    }

    companion object {

        private val CLASSES = arrayOf<Class<*>>(GoogleSignInActivity::class.java)

        private val DESCRIPTION_IDS = intArrayOf(R.string.desc_google_sign_in)
    }
}
