package com.pickle.server.notification.domain;

import com.pickle.server.user.domain.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {
    @Id
    @Column(name ="notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String  relatedUrl;

    @Column(nullable = false)
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void read() {
        isRead = true;
    }
}
