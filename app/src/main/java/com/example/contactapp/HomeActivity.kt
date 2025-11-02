package com.example.contactapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.contactapp.databinding.ActivityHomeBinding
import java.io.File
import java.io.FileOutputStream

class HomeActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_CODE = 100
    private val IMAGE_PICK_CODE = 103
    private val IMAGE_CAPTURE_CODE = 102
    private lateinit var adapter: ContactAdaptar
    private lateinit var binding: ActivityHomeBinding

    val contacts = mutableListOf<ContactModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ContactAdaptar(contacts) { position ->
            contacts.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position, contacts.size)
            updateUI()
        }

        binding.contactRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.contactRecyclerView.adapter = adapter

        updateUI()

        binding.add.setOnClickListener {
            binding.add.hide()

            val fragment = button_sheet_fragment(object : OnContactAddedListener {
                override fun onContactAdded(contact: ContactModel) {
                    adapter.addContact(contact)
                    updateUI()
                    binding.add.show()
                }
            })

            supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit()
        }

        checkAndRequestPermissions()
    }

    private fun updateUI() {
        if (contacts.isEmpty()) {
            binding.homeImage.visibility = View.VISIBLE
            binding.noContactsText.visibility = View.VISIBLE
            binding.contactRecyclerView.visibility = View.GONE
        } else {
            binding.homeImage.visibility = View.GONE
            binding.noContactsText.visibility = View.GONE
            binding.contactRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_CAPTURE_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val path = saveImageToInternalStorage(imageBitmap)

                }

                IMAGE_PICK_CODE -> {
                    val imageUri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    val path = saveImageToInternalStorage(bitmap)

                }
            }
        }
    }

    interface OnContactAddedListener {
        fun onContactAdded(contact: ContactModel)
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val filename = "${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        return file.absolutePath
    }
}
