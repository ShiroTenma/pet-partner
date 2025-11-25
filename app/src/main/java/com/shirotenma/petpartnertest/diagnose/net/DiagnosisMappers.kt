package com.shirotenma.petpartnertest.diagnose.net

import com.shirotenma.petpartnertest.chatbot.ChatContext

fun DiagnosisDto.toChatContext(): ChatContext = ChatContext(
    species = species,
    diseaseCode = detail_class ?: global_class,
    diseaseConfidence = (detail_conf ?: global_conf)?.toFloat(),
    isSupportedAnimal = is_supported_animal ?: (species != null)
)
