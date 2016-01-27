package kotlin.test

import android.app.Activity
import android.os.Bundle

import kotlinx.android.synthetic.main.main.*

class MainActivity : Activity()
{
    /** Called when the activity is first created. */
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        this_is_test.setText("Wow?")
    }
}
