package com.mipa.f1stat.common.api.interceptors

import com.mipa.f1stat.common.config.ApiConfig
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class CertificateCheckInterceptor() : Interceptor {

    private val baseUrl: String = ApiConfig.BASE_URL

    override fun intercept(chain: Interceptor.Chain): Response {
        if (chain.connection()?.handshake() != null && chain.request().url.toString()
                .startsWith(baseUrl)
        ) {
            for (certificate in chain.connection()?.handshake()?.peerCertificates.orEmpty()) {
                Timber.d("CertificateCheckInterceptor")
                val checkedPin = CertificatePinner.pin(certificate)
                if (!ApiConfig.certificatePins.contains(checkedPin)) {

                    Timber.tag(CERTIFICATE_EXCEPTION_TAG)
                        .e("!!! intercept: certificatePins !!! - %s", checkedPin)

                    throw IOException("Certificate Error!")
                }
            }
        }
        return chain.proceed(chain.request())
    }

    companion object {
        private const val CERTIFICATE_EXCEPTION_TAG = "certificateExceptionTag"
    }
}