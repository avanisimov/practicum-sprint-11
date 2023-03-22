package ru.yandex.practicum.sprint11

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //adb shell dumpsys activity activities
        supportActionBar?.title = this.javaClass.simpleName

        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(
                Intent(this@MainActivity, MainActivity2::class.java)
            )
        }
    }
}
