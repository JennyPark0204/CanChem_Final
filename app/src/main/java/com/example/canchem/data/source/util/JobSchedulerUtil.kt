package com.example.canchem.data.source.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.example.canchem.data.source.service.TokenRefreshJobService

object JobSchedulerUtil {

    fun scheduleJob(context: Context, jobId: Int) {
        val componentName = ComponentName(context, TokenRefreshJobService::class.java)
        val builder = JobInfo.Builder(jobId, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // 네트워크 연결 필요
            .setPersisted(true) // 재부팅 후에도 유지

        builder.setMinimumLatency(25 * 60 * 1000) // 최소 25분 후 실행

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }
}