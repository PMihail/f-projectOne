package com.mipa.f1stat.common.di.modules

import android.content.Context
import com.mipa.f1stat.common.helpers.VibrationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class VibrationModule @Inject constructor() {

    @Provides
    @Singleton
    fun provideVibrationHelper(@ApplicationContext ctx: Context): VibrationHelper {
        return VibrationHelper(ctx)
    }

}