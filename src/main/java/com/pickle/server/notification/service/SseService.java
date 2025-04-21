package com.pickle.server.notification.service;

import com.pickle.server.notification.domain.Notification;
import com.pickle.server.notification.domain.NotificationType;
import com.pickle.server.notification.dto.NoticeResDto;
import com.pickle.server.notification.repository.EmitterRepository;
import com.pickle.server.notification.repository.NotificationRepository;
import com.pickle.server.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class SseService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final Long timeoutMillis = 600_000L;

    public SseEmitter subscribe(Long userId, String lastEventId){
        String emitterId = makeId(userId);
        SseEmitter emitter = emitterRepository.saveEmitter(emitterId, new SseEmitter(timeoutMillis));
        emitter.onCompletion(() -> emitterRepository.deleteEmitterById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteEmitterById(emitterId));

        // 첫 연결 메세지 send
        String noticeId = makeId(userId);
        sendNotification(emitter, noticeId, emitterId, String.format("EventStream Created. [userId=%d]",userId));

        if (hasLostData(lastEventId)){
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    public void notice(User user, NotificationType noticeType, String content, String relatedUrl) {
        Notification notification = notificationRepository.save(createNotification(user, noticeType, content, relatedUrl));
        String userId = String.valueOf(user.getId());
        String noticeId = makeId(user.getId());

        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterByUserId(userId);
        emitters.forEach(
                (id, emitter) -> {
                    emitterRepository.saveNoticeCache(id, notification);
                    sendNotification(emitter, noticeId, id, NoticeResDto.convertFromEntity(notification));
                }
        );
    }

    private void sendNotification(SseEmitter emitter, String noticeId, String emitterId, Object data){
        try {
            emitter.send(SseEmitter.event()
                    .id(noticeId)
                    .data(data));
        } catch (IOException e) {
            emitterRepository.deleteEmitterById(emitterId);
        }
    }

    private String makeId(Long id){
        return id.toString() + "_" + System.currentTimeMillis();
    }

    private boolean hasLostData(String lastEventId){
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter){
        Map<String, Object> noticeCaches = emitterRepository
                .findAllNoticeCacheByUserId(String.valueOf(userId));
        noticeCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private Notification createNotification(User user, NotificationType notificationType, String content, String url){
        return Notification.builder()
                .user(user)
                .notificationType(notificationType)
                .content(content)
                .relatedUrl(url)
                .isRead(false)
                .build();
    }

}
