package com.empmanage.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(employee: EmployeeEntity): Long

    @Update
    suspend fun update(employee: EmployeeEntity)

    @Delete
    suspend fun delete(employee: EmployeeEntity)

    @Query("SELECT * FROM employees ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Long): EmployeeEntity?

    @Query("SELECT email FROM employees")
    suspend fun getAllEmails(): List<String>

    @Query("SELECT normalized_phone FROM employees")
    suspend fun getAllNormalizedPhones(): List<String>

    @Query("SELECT fullName FROM employees")
    suspend fun getAllFullNames(): List<String>

    /**
     * Cursor-ready page fetch. Not currently called — the list screen pages
     * the already filtered/sorted in-memory list instead (see
     * EmployeeListViewModel) since search/filter already scan it fully via
     * combine(). Kept as the seam for a future DB-level pagination path if
     * the dataset ever grows large enough to need one.
     */
    @Query("SELECT * FROM employees ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPage(limit: Int, offset: Int): List<EmployeeEntity>
}
