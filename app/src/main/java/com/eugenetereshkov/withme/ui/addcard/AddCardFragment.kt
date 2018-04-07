package com.eugenetereshkov.withme.ui.addcard


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eugenetereshkov.withme.GlideApp
import com.eugenetereshkov.withme.R
import com.eugenetereshkov.withme.extension.bindTo
import com.eugenetereshkov.withme.presentation.addcard.AddCardViewModel
import com.eugenetereshkov.withme.ui.global.BaseFragment
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_add_card.*
import org.koin.android.architecture.ext.viewModel
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class AddCardFragment : BaseFragment() {

    private companion object {
        const val REQUEST_GALLERY = 2
    }

    override val idResLayout: Int = R.layout.fragment_add_card

    private val viewModel: AddCardViewModel by viewModel()
    private val disposable = CompositeDisposable()
    private val clickListener = { view: View ->
        when (view.id) {
            imageViewAddImage.id -> makePickRequestWithPermissionCheck()
            buttonSave.id -> viewModel.saveCard(editTextMessage.text.toString())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.apply {
            title = getString(R.string.create_card)
            setNavigationOnClickListener { viewModel.onBackPressed() }
        }

        viewModel.loadingLiveData.observe(this@AddCardFragment, Observer { show ->
            show?.let {
                viewLoadingHolder.post {
                    viewLoadingHolder?.isVisible = it
                    progressBar?.isVisible = it
                    editTextMessage.isVisible = it.not()

                    if (it.not()) editTextMessage.requestFocus()
                }
            }
        })

        viewModel.uploadProgressLiveData.observe(this@AddCardFragment, Observer { progress ->
            progress?.let { progressBar.progress = it }
        })

        imageViewAddImage.setOnClickListener(clickListener)
        buttonSave.setOnClickListener(clickListener)
    }

    override fun onStart() {
        super.onStart()

        editTextMessage.textChanges()
                .filter { it.toString().trim().isNotEmpty() }
                .subscribe { buttonSave.isVisible = true }
                .bindTo(disposable)
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GALLERY && data != null) {
            val selectImageURI = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            activity?.let {
                it.contentResolver.query(selectImageURI, filePathColumn, null, null, null).use { cursor ->
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val imageDecode = cursor.getString(columnIndex)
                    setUserImage(imageDecode)
                    viewModel.uploadImageToServer(imageDecode)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun makePickRequest() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun setUserImage(url: String?) {
        GlideApp.with(this@AddCardFragment)
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewAddImage)
    }
}
