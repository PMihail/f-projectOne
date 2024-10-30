package com.mipa.f1stat.common.config

import com.mipa.f1stat.BuildConfig

object ApiConfig {

    const val connectionTimeout: Long = 60 * 1000L // 1 min
    const val readTimeout: Long = 60 * 1000L // 1 min
    const val writeTimeout: Long = 60 * 1000L // 1 min
    val certificatePins: List<String> = listOf()

    const val BASE_URL = BuildConfig.baseUrl
}