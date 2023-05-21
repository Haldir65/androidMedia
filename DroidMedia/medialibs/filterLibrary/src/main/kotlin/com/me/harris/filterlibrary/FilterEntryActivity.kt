package com.me.harris.filterlibrary

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.awesomelib.viewBinding
import com.me.harris.filterlibrary.baisc.FilterBasicActivity
import com.me.harris.filterlibrary.databinding.ActivityFilterEntryBinding

class FilterEntryActivity:AppCompatActivity(R.layout.activity_filter_entry) {

    private val binding by viewBinding(ActivityFilterEntryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.card1.setOnClickListener {
            startActivity(Intent(this,FilterBasicActivity::class.java))
        }

    }


}