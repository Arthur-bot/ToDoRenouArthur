package com.todorenouarthur.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import com.todorenouarthur.R
import com.todorenouarthur.network.Api
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class  UserInfoActivity : AppCompatActivity() {

    val mediaStore by lazy { MediaStoreRepository(this) }
    private lateinit var photoUri: Uri
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if(it != null) {
        handleImage(it)
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val avatar = this.findViewById<ImageView>(R.id.image_view)
        val takePictureButton = this.findViewById<Button>(R.id.take_picture_button)
        val browseGalleryButton = this.findViewById<Button>(R.id.upload_image_button)

        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()
            avatar.load(userInfo?.avatar) {
                // affiche une image par défaut en cas d'erreur:
                error(R.drawable.ic_launcher_background)
            }
        }

        takePictureButton.setOnClickListener {
            launchCameraWithPermission()
        }

        browseGalleryButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private val permissionAndCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        // pour simplifier on ne fait rien ici, il faudra que le user re-clique sur le bouton
    }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        when {
            mediaStore.canWriteSharedEntries() && isAlreadyAccepted -> launchCamera()
            isExplanationNeeded -> showExplanation()
            else -> permissionAndCameraLauncher.launch(arrayOf(camPermission, storagePermission))
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(this)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> /* ouvrir les paramètres de l'app */ }
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    private fun handleImage(imageUri: Uri) {
        this.findViewById<ImageView>(R.id.image_view).load(imageUri)
    }

    // register
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
        val view = this.findViewById<Button>(R.id.take_picture_button)
            if (accepted) handleImage(photoUri)
            else Snackbar.make(view, "Échec!", Snackbar.LENGTH_LONG).show()
    }

    private fun launchCamera() {
        lifecycleScope.launch {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow()
            cameraLauncher.launch(photoUri)
        }
    }
}