package com.pickle.server.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository{
    SseEmitter saveEmitter(String emitterId, SseEmitter sseEmitter);
    void saveNoticeCache(String noticeCacheId, Object notice);

    Map<String, SseEmitter> findAllEmitterByUserId(String userId);
    Map<String, Object> findAllNoticeCacheByUserId(String userId);

    void deleteEmitterById(String emitterId);
    void deleteAllEmitterByUserId(String userId);
    void deleteAllNoticeCacheByUserId(String userId);
}
