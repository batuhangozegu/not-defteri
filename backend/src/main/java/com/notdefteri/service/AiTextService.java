package com.notdefteri.service;

import org.springframework.stereotype.Service;

/** Seçili metin üzerinde çalışan AI aksiyonları: özetle, genişlet, düzelt. */
@Service
public class AiTextService {

    private final GeminiClient geminiClient;

    public AiTextService(GeminiClient geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String summarize(String text) {
        return geminiClient.generateContent(
                "Aşağıdaki metni Türkçe olarak, anlamını koruyarak kısa ve net biçimde özetle. " +
                        "Sadece özeti döndür, başka açıklama ekleme.\n\nMetin:\n" + text);
    }

    public String expand(String text) {
        return geminiClient.generateContent(
                "Aşağıdaki metni Türkçe olarak genişlet: gerekçe, örnek ve gerekirse bir sonraki " +
                        "adımı ekleyerek daha ayrıntılı hale getir. Sadece genişletilmiş metni döndür.\n\nMetin:\n" + text);
    }

    public String fix(String text) {
        return geminiClient.generateContent(
                "Aşağıdaki metnin yazım ve dil bilgisi hatalarını düzelt, anlamını değiştirme. " +
                        "Sadece düzeltilmiş metni döndür.\n\nMetin:\n" + text);
    }
}
