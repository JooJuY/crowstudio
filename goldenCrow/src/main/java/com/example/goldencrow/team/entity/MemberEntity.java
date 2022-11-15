package com.example.goldencrow.team.entity;

import com.example.goldencrow.user.UserEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Table(name = "member")
@Data
public class MemberEntity {

    //pk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long memberSeq;

    @ManyToOne
    @JoinColumn(name = "userSeq", referencedColumnName = "userSeq")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "teamSeq", referencedColumnName = "teamSeq")
    private TeamEntity team;

    public MemberEntity() {
    }

    public MemberEntity(UserEntity userEntity, TeamEntity teamEntity) {
        this.user = userEntity;
        this.team = teamEntity;
    }
}
