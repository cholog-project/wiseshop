package cholog.wiseshop.db.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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
}
