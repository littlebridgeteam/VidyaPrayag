package com.littlebridge.enrollplus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.littlebridge.enrollplus.feature.library.data.local.LibraryBookDao
import com.littlebridge.enrollplus.feature.library.data.local.LibraryBookEntity
import com.littlebridge.enrollplus.feature.library.data.local.LibraryCacheDao
import com.littlebridge.enrollplus.feature.library.data.local.LibraryCacheEntity
import com.littlebridge.enrollplus.feature.library.data.local.LibraryPendingActionDao
import com.littlebridge.enrollplus.feature.library.data.local.LibraryPendingActionEntity
import com.littlebridge.enrollplus.feature.event.data.local.EventCacheDao
import com.littlebridge.enrollplus.feature.event.data.local.EventCacheEntity
import com.littlebridge.enrollplus.feature.event.data.local.EventOutboxDao
import com.littlebridge.enrollplus.feature.event.data.local.EventOutboxEntity
import com.littlebridge.enrollplus.feature.schools.data.local.SchoolDao
import com.littlebridge.enrollplus.feature.schools.data.local.SchoolEntity
import com.littlebridge.enrollplus.feature.teacher.data.local.OutboxOperationDao
import com.littlebridge.enrollplus.feature.teacher.data.local.OutboxOperationEntity
import com.littlebridge.enrollplus.feature.teacher.data.local.TeacherDayCacheDao
import com.littlebridge.enrollplus.feature.teacher.data.local.TeacherDayCacheEntity
import com.littlebridge.enrollplus.feature.announcements.data.local.AnnouncementDao
import com.littlebridge.enrollplus.feature.announcements.data.local.AnnouncementEntity

@Database(
    entities = [
        SchoolEntity::class,
        LibraryBookEntity::class,
        LibraryCacheEntity::class,
        LibraryPendingActionEntity::class,
        EventCacheEntity::class,
        EventOutboxEntity::class,
        OutboxOperationEntity::class,
        AnnouncementEntity::class,
        TeacherDayCacheEntity::class,
    ],
    version = 3,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun schoolDao(): SchoolDao
    abstract fun libraryBookDao(): LibraryBookDao
    abstract fun libraryCacheDao(): LibraryCacheDao
    abstract fun libraryPendingActionDao(): LibraryPendingActionDao
    abstract fun eventCacheDao(): EventCacheDao
    abstract fun eventOutboxDao(): EventOutboxDao
    abstract fun outboxOperationDao(): OutboxOperationDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun teacherDayCacheDao(): TeacherDayCacheDao

    companion object
}

// Room generator will provide this on iOS
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect fun AppDatabase.Companion.instantiateImpl(): AppDatabase
