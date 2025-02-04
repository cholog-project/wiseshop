package cholog.wiseshop.db.member;

import cholog.wiseshop.api.member.domain.MemberModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Table(name = "MEMBER")
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "member_email", nullable = false)
    private String email;

    @NotNull
    @Column(name = "member_name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    public Member(Long id,
                  String email,
                  String name,
                  String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Member(String email,
                  String name,
                  String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public MemberModel toModel() {
        return new MemberModel(
                name,
                email,
                List.of(), // todo: address 추가
                List.of() // todo : payments 추가 (결제수단)
        );
    }
}
