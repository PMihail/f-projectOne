package com.mipa.f1stat.common.di.modules

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mipa.f1stat.common.api.interceptors.CertificateCheckInterceptor
import com.mipa.f1stat.common.config.ApiConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@InstallIn(SingletonComponent::class)
@Module
class RetrofitModule {

    private val baseUrl: String = ApiConfig.BASE_URL
    private val timberTag = "okhttpTag"

    @Provides
    @Singleton
    fun provideRetrofit(builder: Retrofit.Builder): Retrofit {
        return builder
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create()
    }

    @Provides
    fun provideHttpClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(
                ApiConfig.connectionTimeout,
                TimeUnit.MILLISECONDS
            )
            .readTimeout(ApiConfig.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(ApiConfig.writeTimeout, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)

        okHttpClient.addNetworkInterceptor(HttpLoggingInterceptor { message ->
            Timber.tag(timberTag).d(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        if (ApiConfig.certificatePins.isEmpty()) {
            val trustManager = createProvideNonValidateTrustManager()
            var sslContext: SSLContext? = null
            try {
                sslContext = SSLContext.getInstance("SSL")
                sslContext?.init(null, arrayOf(trustManager), java.security.SecureRandom())
            } catch (e: Exception) {
                Timber.tag(timberTag).e(e)
            }

            okHttpClient.hostnameVerifier { _, _ -> true }
            sslContext?.socketFactory?.let { okHttpClient.sslSocketFactory(it, trustManager) }
        } else {
            okHttpClient.addNetworkInterceptor(CertificateCheckInterceptor())
        }

        return okHttpClient.build()
    }

    @Provides
    internal fun provideRetrofitBuilder(
        converterFactory: Converter.Factory,
        okHttpClient: OkHttpClient
    ): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
    }

    @Provides
    internal fun provideConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory
            .create(gson)
    }

    private fun createProvideNonValidateTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        }
    }
}