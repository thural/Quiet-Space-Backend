package dev.thural.quietspacebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Integer version;

    private String userId;

    private String text;

    private List<UUID> likes;

    private OffsetDateTime createDate = OffsetDateTime.now();

    private OffsetDateTime updateDate = OffsetDateTime.now();

    @PrePersist
    private void onCreate() {
        createDate = OffsetDateTime.now();
        updateDate = OffsetDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updateDate = OffsetDateTime.now();
    }

}