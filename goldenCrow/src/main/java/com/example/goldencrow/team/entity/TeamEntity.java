package com.example.goldencrow.team.entity;

import com.example.goldencrow.user.entity.UserEntity;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@DynamicUpdate
@Table(name = "team")
@Data
public class TeamEntity {

    //pk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long teamSeq;

    @Column
    private String teamName;

    @ManyToOne
    @JoinColumn(name="teamLeaderSeq", referencedColumnName = "userSeq")
    private UserEntity teamLeader;

    public TeamEntity() {
    }

    public TeamEntity(String teamName) {
        this.teamName = teamName;
        // User들은 서비스에서 등록하자~
    }
}