package com.cezila.peerwhisper.core.di

import android.content.Context
import com.cezila.peerwhisper.feature.wifidirect.IWifiDirectManager
import com.cezila.peerwhisper.feature.wifidirect.IWifiDirectService
import com.cezila.peerwhisper.feature.wifidirect.WifiDirectManagerImpl
import com.cezila.peerwhisper.feature.wifidirect.WifiDirectServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WifiDirectModule {

    @Provides
    @Singleton
    fun provideWifiDirectService(
        @ApplicationContext context: Context
    ): IWifiDirectService {
        return WifiDirectServiceImpl(context)
    }

    @Provides
    @Singleton
    fun provideWifiDirectManager(
        @ApplicationContext context: Context,
        wifiDirectService: IWifiDirectService
    ): IWifiDirectManager {
        return WifiDirectManagerImpl(context, wifiDirectService)
    }
}