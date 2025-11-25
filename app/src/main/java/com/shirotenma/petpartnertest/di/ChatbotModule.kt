package com.shirotenma.petpartnertest.di

import com.shirotenma.petpartnertest.chatbot.ChatbotEngine
import com.shirotenma.petpartnertest.chatbot.DiseaseKnowledgeBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatbotModule {

    @Provides
    @Singleton
    fun provideChatbotEngine(): ChatbotEngine =
        ChatbotEngine(DiseaseKnowledgeBase.items)
}
