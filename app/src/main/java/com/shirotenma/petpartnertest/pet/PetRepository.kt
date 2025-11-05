package com.shirotenma.petpartnertest.pet

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetRepository @Inject constructor(
    private val dao: PetDao
) {
    fun observePets(): Flow<List<Pet>> = dao.observeAll()
    fun observePet(id: Long): Flow<Pet?> = dao.observeById(id)
    suspend fun upsert(pet: Pet) = dao.upsert(pet)
    suspend fun delete(pet: Pet) = dao.delete(pet)
}
