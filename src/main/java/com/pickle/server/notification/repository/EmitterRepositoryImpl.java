package com.pickle.server.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository{
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> noticeCache = new ConcurrentHashMap<>();

    /*
    Emitter 관련 repository
     */
    @Override
    public SseEmitter saveEmitter(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterByUserId(String userId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteEmitterById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteAllEmitterByUserId(String userId) {
        List<String> emitterIds = emitters.keySet().stream()
                .filter(key -> key.startsWith(userId)).collect(Collectors.toList());

        emitterIds.forEach(emitters::remove);
    }


    /*
    Notice 관련 Repository
     */
    @Override
    public void saveNoticeCache(String noticeCacheId, Object notice) {
        noticeCache.put(noticeCacheId, notice);
    }

    @Override
    public Map<String, Object> findAllNoticeCacheByUserId(String userId) {
        return noticeCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteAllNoticeCacheByUserId(String userId) {
        List<String> noticeIds = noticeCache.keySet().stream()
                .filter(key -> key.startsWith(userId))
                .collect(Collectors.toList());

        noticeIds.forEach(noticeCache::remove);
    }

}
