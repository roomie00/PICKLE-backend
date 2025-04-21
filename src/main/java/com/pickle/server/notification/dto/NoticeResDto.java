package com.pickle.server.notification.dto;

import com.pickle.server.notification.domain.Notification;
import com.pickle.server.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeResDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateNotification {
        private Long id;
        private String content;
        private String relatedUrl;
        private NotificationType type;
    }

    public static CreateNotification convertFromEntity(Notification notification) {
        return CreateNotification.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .relatedUrl(notification.getRelatedUrl())
                .type(notification.getNotificationType())
                .build();
    }
}
