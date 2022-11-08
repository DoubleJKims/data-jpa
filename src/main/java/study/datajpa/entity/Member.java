package study.datajpa.entity;

import lombok.Getter;
import study.datajpa.repository.MemberJpaRepository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;

    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }

    public void changeUsername(String username) {
        this.username = username;
    }
}
