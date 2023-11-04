package com.project.e_lakis.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.project.e_lakis.databinding.ActivityReportBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraUploadCallback: ValueCallback<Array<Uri>>? = null
    private var originalOrientation: Int = 0

    companion object {
        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val CAMERA_INPUT_FILE_REQUEST_CODE = 2
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        originalOrientation = requestedOrientation

        // Inisialisasi WebView dan ProgressBar
        val webView = binding.webView
        val progressBar = binding.progressBar

        // Aktifkan JavaScript dalam WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Konfigurasi WebViewClient untuk menampilkan pesan saat halaman dimuat
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
            }
        }

        // Konfigurasi WebChromeClient untuk menangani dialog unggah file
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@ReportActivity.filePathCallback = filePathCallback

                val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
                fileIntent.type = "*/*"  // Set the desired file type here, e.g., "image/*" for images

                val chooserIntent = Intent.createChooser(fileIntent, "Choose a file")
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)

                return true
            }
        }

        // Load Google Form di WebView
        webView.loadUrl("https://form.jotform.com/233044335121442")
    }

    private fun createImageFileUri(): Uri {
        // Membuat file untuk menyimpan foto yang diambil dari kamera
        // Anda perlu mengatur lokasi penyimpanan sesuai dengan kebutuhan aplikasi Anda
        // Di sini kami menggunakan direktori Pictures sebagai contoh
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_",
            ".jpg",
            storageDir
        )

        return FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            imageFile
        )
    }

    // Override metode onActivityResult untuk menangani pemilihan file
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INPUT_FILE_REQUEST_CODE) {
            if (filePathCallback == null) return
            if (resultCode == RESULT_OK) {
                val results = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                filePathCallback?.onReceiveValue(results)
                filePathCallback = null
            } else {
                filePathCallback?.onReceiveValue(null)
                filePathCallback = null
            }

            // Setel kembali orientasi layar ke yang semula
            requestedOrientation = originalOrientation
        }

        if (requestCode == CAMERA_INPUT_FILE_REQUEST_CODE) {
            if (cameraUploadCallback == null) return
            if (resultCode == RESULT_OK) {
                val results = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
                cameraUploadCallback?.onReceiveValue(arrayOf(createImageFileUri()))
                cameraUploadCallback = null
            } else {
                cameraUploadCallback?.onReceiveValue(null)
                cameraUploadCallback = null
            }

            // Setel kembali orientasi layar ke yang semula
            requestedOrientation = originalOrientation
        }
    }
}
