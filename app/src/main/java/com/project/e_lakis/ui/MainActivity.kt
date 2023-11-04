package com.project.e_lakis.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.e_lakis.R
import com.project.e_lakis.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnWisma.setOnClickListener{
            val reportAct = Intent(this@MainActivity, ReportActivity::class.java)
            startActivity(reportAct)
        }

        binding.btnKelas.setOnClickListener{
            val reportAct2 = Intent(this@MainActivity, ReportActivity2::class.java)
            startActivity(reportAct2)
        }

    }
}