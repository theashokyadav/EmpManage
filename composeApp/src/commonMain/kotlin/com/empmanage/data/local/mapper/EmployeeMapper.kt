package com.empmanage.data.local.mapper

import com.empmanage.data.local.db.EmployeeEntity
import com.empmanage.domain.model.Department
import com.empmanage.domain.model.Employee
import com.empmanage.domain.model.EmploymentType
import com.empmanage.domain.model.Gender
import com.empmanage.domain.model.ResumeMetadata
import com.empmanage.domain.model.Skill

fun EmployeeEntity.toDomain(): Employee = Employee(
    id = id,
    fullName = fullName,
    email = email,
    phoneNumber = phoneNumber,
    normalizedPhone = normalizedPhone,
    address = address,
    gender = Gender.valueOf(gender),
    department = Department.valueOf(department),
    skills = skills.mapNotNull { name -> runCatching { Skill.valueOf(name) }.getOrNull() },
    employmentType = EmploymentType.valueOf(employmentType),
    isActive = isActive,
    joiningDateMillis = joiningDateMillis,
    salary = salary,
    profileImagePath = profileImagePath,
    resumePath = resumePath,
    resumeMetadata = if (resumeFileName != null && resumeFileSizeBytes != null && resumeMimeType != null) {
        ResumeMetadata(resumeFileName, resumeFileSizeBytes, resumeMimeType)
    } else {
        null
    },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Employee.toEntity(): EmployeeEntity = EmployeeEntity(
    id = id,
    fullName = fullName,
    email = email,
    phoneNumber = phoneNumber,
    normalizedPhone = normalizedPhone,
    address = address,
    gender = gender.name,
    department = department.name,
    skills = skills.map { it.name },
    employmentType = employmentType.name,
    isActive = isActive,
    joiningDateMillis = joiningDateMillis,
    salary = salary,
    profileImagePath = profileImagePath,
    resumePath = resumePath,
    resumeFileName = resumeMetadata?.fileName,
    resumeFileSizeBytes = resumeMetadata?.fileSizeBytes,
    resumeMimeType = resumeMetadata?.mimeType,
    createdAt = createdAt,
    updatedAt = updatedAt
)
