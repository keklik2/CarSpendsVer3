package com.demo.carspends.presentation.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo.carspends.R

class DetailElementsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_elements)
    }

    companion object {
        fun startAddOrEditNoteExtraIntent(): Intent { return Intent() }

        fun startAddOrEditNoteRepairIntent(): Intent { return Intent() }

        fun startAddOrEditNoteFillingIntent(): Intent { return Intent() }

        fun startAddOrEditComponentIntent(): Intent { return Intent() }

    }
}