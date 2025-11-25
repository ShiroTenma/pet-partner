package com.shirotenma.petpartnertest.diagnose.net

import com.shirotenma.petpartnertest.chatbot.ChatContext

fun DiagnosisDto.toChatContext(): ChatContext {
    val globalConfVal = global_conf ?: 0.0
    val threshold = unknown_threshold ?: 0.6
    val supported = (is_supported_animal == true) &&
            !species.isNullOrBlank() &&
            globalConfVal >= threshold

    return ChatContext(
        species = species,
        diseaseCode = detail_class ?: global_class,
        diseaseConfidence = (detail_conf ?: global_conf)?.toFloat(),
        isSupportedAnimal = supported
    )
}
