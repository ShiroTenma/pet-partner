package com.shirotenma.petpartnertest.schedule

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val dao: ScheduleDao
) {
    fun schedules(petId: Long): Flow<List<Schedule>> = dao.observeByPet(petId)
    suspend fun get(id: Long): Schedule? = dao.getById(id)

    suspend fun upsert(schedule: Schedule): Long {
        return if (schedule.id == 0L) {
            dao.insert(schedule)
        } else {
            dao.update(schedule)
            schedule.id
        }
    }

    suspend fun delete(id: Long) = dao.deleteById(id)
}
