package com.me.harris.filterlibrary.freetypes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.me.harris.filterlibrary.databinding.ActivityFreeTypesBinding

class FreeTypesDisplayTextActivity:AppCompatActivity() {


    private lateinit var binding:ActivityFreeTypesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFreeTypesBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
