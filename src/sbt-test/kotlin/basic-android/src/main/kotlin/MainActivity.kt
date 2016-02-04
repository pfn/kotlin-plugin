package sbt.kotlin.test

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity()
{
    /** Called when the activity is first created. */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }
}
