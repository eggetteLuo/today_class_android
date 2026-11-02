package com.eggetteluo.todayclass.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "course_schedule_week_note",
    primaryKeys = ["scheduleId", "weekNo"],
    foreignKeys = [
        ForeignKey(
            entity = CourseScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["scheduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scheduleId"])
    ]
)
data class CourseScheduleWeekNoteEntity(
    val scheduleId: Long,
    val weekNo: Int,
    val note: String,
    val updatedAt: Long
)
