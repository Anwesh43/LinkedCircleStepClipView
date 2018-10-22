package com.anwesh.uiprojects.linkedcirclestepclipview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.circlestepclipview.CircleStepClipView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CircleStepClipView.create(this)
    }
}
