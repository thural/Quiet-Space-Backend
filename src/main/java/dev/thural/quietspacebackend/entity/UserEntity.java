package dev.thural.quietspacebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Column(length = 16)
    private String role;

    @NotNull
    @NotBlank
    @Column(length = 32, unique = true)
    private String username;

    @NotNull
    @NotBlank
    @Column(length = 32, unique = true)
    private String email;

    @NotNull
    @NotBlank
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLikeEntity> postLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLikeEntity> commentLikes;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatEntity> chats;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageEntity> messages;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowEntity> followings;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FollowEntity> followers;

    @NotNull
    @Column(updatable = false)
    private OffsetDateTime createDate;

    @NotNull
    private OffsetDateTime updateDate;

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