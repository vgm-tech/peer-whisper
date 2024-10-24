package com.cezila.peerwhisper.core.di

import com.cezila.peerwhisper.feature.chatcommunication.repository.ChatCommunicationRepositoryImpl
import com.cezila.peerwhisper.feature.chatcommunication.repository.IChatCommunicationRepository
import com.cezila.peerwhisper.feature.chatcommunication.service.ISocketService
import com.cezila.peerwhisper.feature.chatcommunication.service.SocketServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatCommunicationModule {

    @Provides
    @Singleton
    fun provideSocketService(): ISocketService {
        return SocketServiceImpl()
    }

    @Provides
    @Singleton
    fun provideChatCommunicationRepository(service: ISocketService): IChatCommunicationRepository {
        return ChatCommunicationRepositoryImpl(service)
    }

}