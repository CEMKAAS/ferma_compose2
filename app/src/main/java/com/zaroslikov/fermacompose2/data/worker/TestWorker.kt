package com.zaroslikov.fermacompose2.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zaroslikov.domain.repository.BookmarkRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class TestWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.i("work_incubator", "TEST WORKER STARTED")
        val bookmarkId = inputData.getLong("bookmarkId", -1)
        val time = inputData.getString("time") ?: return Result.success()


        if (bookmarkId == -1L) return Result.success()
        val bookmarkRepository = EntryPointAccessors.fromApplication(
            applicationContext,
            BookmarkRepository::class.java
        ).getBookmarkForWork(bookmarkId)
        Log.i("work_incubator", "bookmark ${bookmarkId}")
        return Result.success()
    }
}