package com.example.disiprojectbackend.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "senderMessage_id", referencedColumnName = "id", nullable = true)
    private User senderMessage;

    @ManyToOne
    @JoinColumn(name = "receiverMessage_id", referencedColumnName = "id", nullable = true)
    private User receiverMessage;


    @Column(name = "sent_at", updatable = false)
    private Date sentAt;

    @Column(name = "content", nullable = false)
    private String content;
}
