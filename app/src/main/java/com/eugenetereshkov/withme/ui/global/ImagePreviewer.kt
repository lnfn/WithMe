package com.eugenetereshkov.withme.ui.global

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.eugenetereshkov.withme.R


@SuppressLint("ClickableViewAccessibility")
fun ImageView.show(context: Context) {
    val background = this.rootView.getBlurredScreenDrawable(context)

    val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_preview_card, null)
    val imageView = dialogView.findViewById(R.id.imageView) as ImageView

    val copy = this.drawable.constantState.newDrawable()
    imageView.setImageDrawable(copy)

    val dialog = Dialog(context, R.style.FullScreenDialogStyle).apply {
        window.setBackgroundDrawable(background)
        setContentView(dialogView)
    }

    dialog.show()
}

fun View.getBlurredScreenDrawable(context: Context): BitmapDrawable {
    val screenshot = this.takeScreenshot()
    val blurred = screenshot.blurBitmap(context)
    return BitmapDrawable(context.resources, blurred)
}

fun View.takeScreenshot(): Bitmap {
    this.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(this.drawingCache)
    this.isDrawingCacheEnabled = false
    return bitmap
}

fun Bitmap.blurBitmap(context: Context): Bitmap {
    val bitmapScale = 0.3f
    val blurRadius = 10f

    val width = Math.round(this.width * bitmapScale)
    val height = Math.round(this.height * bitmapScale)

    val inputBitmap = Bitmap.createScaledBitmap(this, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)

    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(blurRadius)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    return outputBitmap
}