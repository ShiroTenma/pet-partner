package com.shirotenma.petpartnertest.pet

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface PetRepository {
    fun observePets(): Flow<List<Pet>>
    fun observePet(id: Long): Flow<Pet?>
    suspend fun upsert(pet: Pet): Long
    suspend fun delete(pet: Pet)
}

@Singleton
class PetRepositoryImpl @Inject constructor(
    private val dao: PetDao
) : PetRepository {
    override fun observePets(): Flow<List<Pet>> = dao.observeAll()
    override fun observePet(id: Long): Flow<Pet?> = dao.observeById(id)
    override suspend fun upsert(pet: Pet): Long = dao.upsert(pet)
    override suspend fun delete(pet: Pet) = dao.delete(pet)
}
