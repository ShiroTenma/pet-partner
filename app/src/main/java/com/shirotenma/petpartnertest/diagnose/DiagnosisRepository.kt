// app/src/main/java/com/shirotenma/petpartnertest/diagnose/DiagnosisRepository.kt
package com.shirotenma.petpartnertest.diagnose

import android.content.Context
import android.net.Uri
import com.shirotenma.petpartnertest.data.ApiService
import com.shirotenma.petpartnertest.data.DiagnoseReq
import com.shirotenma.petpartnertest.data.DiagnoseResp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosisRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun diagnose(ctx: Context, petId: Long, photo: Uri): DiagnoseResp =
        withContext(Dispatchers.IO) {
            val bytes = ImageUtil.readBytes(ctx, photo)
            val b64 = if (bytes.isNotEmpty()) ImageUtil.toBase64(bytes) else null
            api.diagnose(DiagnoseReq(petId = petId, imageBase64 = b64))
        }
}
