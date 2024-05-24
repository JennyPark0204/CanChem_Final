package com.example.canchem.data.source.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.example.canchem.data.source.service.NewTokenRefreshJobService

object NewJobSchedulerUtil {
    private const val JOB_ID = 1

    fun scheduleJob(context: Context) {
        val componentName = ComponentName(context, NewTokenRefreshJobService::class.java)
        val builder = JobInfo.Builder(JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // 네트워크 연결 필요
            .setPersisted(true) // 재부팅 후에도 유지

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMinimumLatency(15 * 60 * 1000) // 최소 15분 후 실행
        } else {
            builder.setPeriodic(15 * 60 * 1000) // 15분 간격으로 실행
        }

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }
}