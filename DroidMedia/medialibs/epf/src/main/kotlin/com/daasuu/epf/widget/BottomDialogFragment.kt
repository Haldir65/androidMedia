package com.daasuu.epf.widget

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.daasuu.epf.R
import java.io.Serializable
import java.util.*

class BottomDialogFragment : androidx.fragment.app.DialogFragment() {


    private var mType: Int = 0
    private var mTitle: String? = null
    private var mSelectionIndex: Int = 0
    private var options: List<Option> = ArrayList()

    private lateinit var tv_operate_title:TextView
    private lateinit var ll_container: LinearLayout
    private lateinit var iv_close: ImageView

    companion object {
        private val TAG = "BottomDialogFragment"

        fun getInstance(
            type: Int,
            selection: Int,
            title: String,
            optionList: List<Option>
        ): BottomDialogFragment {
            val dialogFragment = BottomDialogFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putInt("selection", selection)
            bundle.putString("title", title)
            bundle.putSerializable("options", optionList as Serializable)
            dialogFragment.arguments = bundle
            return dialogFragment
        }
    }

    var callback: ((select: Int, option: Option) -> Unit)? = null

    class Option(
        internal var iconResId: Int,
        internal var optionName: String,
        internal var index: Int = 0
    ) : Serializable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            androidx.fragment.app.DialogFragment.STYLE_NORMAL,
            R.style.recordBeautyDialogStyle
        )

        arguments?.run {
            mType = getInt("type")
            mSelectionIndex = getInt("selection")
            mTitle = getString("title")
            options = getSerializable("options") as List<Option>
            Log.d(
                TAG,
                "onCreate: ....type:$mType, mTitle:$mTitle, mSelectionIndex:$mSelectionIndex"
            )
        }

    }

    override fun onStart() {
        super.onStart()
        initBottomSettings()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.bottom_dialog_fragment_layout, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(rootView: View) {
        ll_container = rootView.findViewById(R.id.ll_container)
        tv_operate_title = rootView.findViewById(R.id.tv_operate_title)
        iv_close = rootView.findViewById(R.id.iv_close)
        tv_operate_title.text = mTitle
        options.forEachIndexed { index, option ->
            val itemView =
                LayoutInflater.from(context).inflate(R.layout.item_record_beauty, null)
            itemView.findViewById<ImageView>(R.id.iv_beauty_image)
                .setImageResource(option.iconResId)
            itemView.findViewById<TextView>(R.id.tv_beauty_text).text =
                option.optionName
            itemView.tag = option
            option.index = index
            ll_container.addView(itemView)

            if (index == mSelectionIndex) {
                itemView.findViewById<ImageView>(R.id.iv_beauty_circle).visibility =
                    View.VISIBLE
            }

            itemView.setOnClickListener(listener)
        }

        iv_close.setOnClickListener {
            dismiss()
        }
    }

    fun setSelectionCallBack(_callback: (select: Int, option: Option) -> Unit) {
        callback = _callback
    }

    var listener = object : View.OnClickListener {
        override fun onClick(view: View?) {
            view?.run {
                findViewById<ImageView>(R.id.iv_beauty_circle).visibility = View.VISIBLE
                var option = tag as Option
                var selection = option.index
                putInt(context, "filter_selection", selection)


                var childCount = ll_container.childCount
                var name: String = ""
                for (i in 0 until childCount) {
                    val childAt = ll_container.getChildAt(i)
                    childAt.findViewById<View>(R.id.iv_beauty_circle).visibility =
                        if (i == selection) {
                            View.VISIBLE
                        } else {
                            View.INVISIBLE
                        }
                }

                callback?.invoke(selection, option)
            }
        }
    }
}

private fun androidx.fragment.app.DialogFragment.initBottomSettings(){
    val dialog = dialog?:return
    val window = dialog.window
    window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    val lp = window.attributes
    lp.gravity = Gravity.BOTTOM //底部
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    window.setWindowAnimations(R.style.dialogAnim)
    window.attributes = lp
}

val spname:String = "litedo"


fun putInt(context: Context?, key: String, value: Int) {
    if (context == null) return
    val sp = context.getSharedPreferences(spname, Context.MODE_PRIVATE)
    val editor = sp.edit()
    editor.putInt(key, value)
    editor.commit()
}

fun getInt(context: Context?, key: String, defValue: Int): Int {
    if (context == null) return defValue
    val sp = context.getSharedPreferences(spname, Context.MODE_PRIVATE)
    return sp.getInt(key, defValue)
}