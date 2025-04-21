package com.pickle.server.notification.dto;

import com.pickle.server.notification.domain.NotificationType;
import lombok.Getter;

public class NoticeReqDto {
    @Getter
    public static class CreateNotificationRequest {
        private String content;
        private String relatedUrl;
        private NotificationType type;
    }
}
