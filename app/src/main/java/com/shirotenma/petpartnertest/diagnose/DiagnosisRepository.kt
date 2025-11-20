package com.shirotenma.petpartnertest.diagnose

import com.shirotenma.petpartnertest.data.ApiService
import com.shirotenma.petpartnertest.data.DiagnoseReq
import com.shirotenma.petpartnertest.data.DiagnoseRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context

@Singleton
class DiagnosisRepository @Inject constructor(
    private val api: ApiService,
    @ApplicationContext private val appContext: Context
) {
    /**
     * Panggil endpoint /diagnose pakai model baru:
     *   DiagnoseReq(petId, photoUri)
     * dan balikin DiagnoseRes dari ApiService.
     */
    suspend fun diagnose(
        petId: Long,
        photoUri: String
    ): DiagnoseRes {
        return api.diagnose(
            DiagnoseReq(
                petId = petId,
                photoUri = photoUri
            )
        )
    }
}
