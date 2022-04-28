package com.example.myappalpha.user

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import coil.load
import com.example.myappalpha.R
import com.google.android.material.snackbar.Snackbar

class UserInfoActivity : AppCompatActivity() {
//    private val imageView = view?.findViewById<ImageView>(R.id.image_view)
    private val getPhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        imageView.load(bitmap) // afficher
    }
    private fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }
    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted)
                getPhoto.launch()// lancer l'action souhaitée
            else
                showMessage("Erreur permission camera")// afficher une explication
        }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        requestCamera.launch(camPermission)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
//        val imageView = findViewById<ImageView>(R.id.image_view)

        val buttonTakePicture = findViewById<Button>(R.id.take_picture_button)

//        val lienImage = intent.getStringExtra("lienImage")
//        imageView.load(lienImage)

        buttonTakePicture.setOnClickListener {
            launchCameraWithPermission()
        }
    }
}