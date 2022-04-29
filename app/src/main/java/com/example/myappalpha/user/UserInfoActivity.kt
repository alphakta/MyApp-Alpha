package com.example.myappalpha.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.myappalpha.R
import com.example.myappalpha.network.Api
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.permissions.RequestAccess
import com.google.modernstorage.permissions.StoragePermissions
import com.google.modernstorage.storage.AndroidFileSystem
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*

class UserInfoActivity : AppCompatActivity() {
    private var imageView : ImageView? = null
    // TAKE PICTURE
    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpeg")
        tmpFile.outputStream().use {
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // this est le bitmap dans ce contexte
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

//    private val getPhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
//        imageView?.load(bitmap) // afficher
//        lifecycleScope.launch{
//            Api.userWebService.updateAvatar(bitmap!!.toRequestBody())
//        }
//    }

    private fun showMessage(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            .setAction("Open Settings") {
                val intent = Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                startActivity(intent)
            }
            .show()
    }
//    private val requestCamera =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
//            if (accepted)
//                getPhoto.launch()// lancer l'action souhaitée
//            else
//                showMessage("Erreur permission camera")// afficher une explication
//        }

//    private fun launchCameraWithPermission() {
//        val camPermission = Manifest.permission.CAMERA
//        val permissionStatus = checkSelfPermission(camPermission)
//        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
//        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
//        when {
//            isAlreadyAccepted -> getPhoto.launch()// lancer l'action souhaitée
//            isExplanationNeeded -> showMessage("Erreur permission camera") // afficher une explication
//            else -> requestCamera.launch(camPermission)// lancer la demande de permission
//        }
//    }
    private val fileSystem by lazy { AndroidFileSystem(this) } // pour interagir avec le stockage

    private lateinit var photoUri: Uri // on stockera l'uri dans cette variable
    private val getPhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        // afficher et uploader l'image enregistrée dans `photoUri`
        imageView?.load(photoUri)
        if(success){
            lifecycleScope.launch{
                Api.userWebService.updateAvatar(photoUri!!.toRequestBody())
            }
        }
        else
            showMessage("Erreur image")
    }

    private val requestCamera =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            getPhoto.launch(photoUri)
        }
    private fun launchCameraWithPermissions() {
        requestWriteAccess.launch(
            RequestAccess.Args(
                action = StoragePermissions.Action.READ_AND_WRITE,
                types = listOf(StoragePermissions.FileType.Image),
                createdBy = StoragePermissions.CreatedBy.AllApps
            )
        )
    }
    val requestWriteAccess = registerForActivityResult(RequestAccess()) { accepted ->
        // utiliser le code précédent de `launchCameraWithPermissions`
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> getPhoto.launch(photoUri)// lancer l'action souhaitée
            isExplanationNeeded -> showMessage("Erreur permission camera") // afficher une explication
            else -> requestCamera.launch(camPermission)// lancer la demande de permission
        }
    }

    // UPLOAD IMAGE
    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = fileBody
        )
    }
    // launcher pour la permission d'accès au stockage
    val requestReadAccess = registerForActivityResult(RequestAccess()) { hasAccess ->
        if (hasAccess) {
            // launch gallery
            openGallery()
        } else {
            showMessage("Erreur permission camera") // afficher une explication
        }
    }
    fun openGallery() {
        requestReadAccess.launch(
            RequestAccess.Args(
                action = StoragePermissions.Action.READ,
                types = listOf(StoragePermissions.FileType.Image),
                createdBy = StoragePermissions.CreatedBy.AllApps
            )
        )
    }
    // register
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // au retour de la galerie on fera quasiment pareil qu'au retour de la caméra mais avec une URI àla place du bitmap
        lifecycleScope.launch{
            Api.userWebService.updateAvatar(uri!!.toRequestBody())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
        // créer et stocker l'uri:
        photoUri = fileSystem.createMediaStoreUri(
            filename = "picture-${UUID.randomUUID()}.jpg",
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            directory = "Todo",
        )!!

        val buttonTakePicture = findViewById<Button>(R.id.take_picture_button)
        imageView = findViewById<ImageView>(R.id.image_view)
        val lienImage = intent.getStringExtra("lienImage")
        imageView?.load(lienImage)
        buttonTakePicture.setOnClickListener {
            launchCameraWithPermissions()
        }

        val buttonUploadImage = findViewById<Button>(R.id.upload_image_button)
        buttonUploadImage.setOnClickListener{
            galleryLauncher.launch("image/*")
        }
    }
}
