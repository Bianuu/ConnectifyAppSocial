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
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user1_id", referencedColumnName = "id", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", referencedColumnName = "id", nullable = false)
    private User user2;


    @Column(name = "created_at", updatable = false)
    private Date createdAt;
}
