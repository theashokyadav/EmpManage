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

    @Query("SELECT COUNT(*) FROM employees")
    fun observeCount(): Flow<Int>

    // DB-level pagination (bonus #4): a growing LIMIT re-runs the query reactively
    // as the Flow's collector asks for more (EmployeeListViewModel widens `limit`
    // as the user scrolls). One @Query per sort order because Room/SQLite can't
    // parameterize ORDER BY column/direction via a bind parameter.

    @Query("SELECT * FROM employees ORDER BY fullName COLLATE NOCASE ASC LIMIT :limit")
    fun observeLimitedByNameAsc(limit: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY fullName COLLATE NOCASE DESC LIMIT :limit")
    fun observeLimitedByNameDesc(limit: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY joiningDateMillis DESC LIMIT :limit")
    fun observeLimitedByJoiningNewest(limit: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY joiningDateMillis ASC LIMIT :limit")
    fun observeLimitedByJoiningOldest(limit: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY salary DESC LIMIT :limit")
    fun observeLimitedBySalaryHigh(limit: Int): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY salary ASC LIMIT :limit")
    fun observeLimitedBySalaryLow(limit: Int): Flow<List<EmployeeEntity>>
}
