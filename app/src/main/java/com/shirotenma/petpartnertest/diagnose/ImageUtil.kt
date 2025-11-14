// app/src/main/java/com/shirotenma/petpartnertest/diagnose/ImageUtil.kt
package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import android.net.Uri
import android.util.Base64

object ImageUtil {
    fun readBytes(ctx: Context, uri: Uri): ByteArray =
        ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)

    fun toBase64(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)
}
