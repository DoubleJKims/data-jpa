package study.datajpa.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("testEntity")
    void testEntity() throws Exception {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        //초기화
        em.flush();
        em.clear();
        //when

        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

        for(Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }

    @Test
    @DisplayName("jpaEventBaseEntity")
    void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist 발생

        Thread.sleep(100);
        member.changeUsername("member2");

        em.flush(); //@PreUpdate 발생
        em.clear();

        //when
        Optional<Member> byId = memberRepository.findById(member.getId());
        Member findMember = byId.get();

        //then
        System.out.println("findMember createDate = " + findMember.getCreateDate());
        //System.out.println("findMember updateDate = " + findMember.getUpdateDate());
        System.out.println("findMember updateDate = " + findMember.getLastModifiedDate());
        System.out.println("findMember createdBy = " + findMember.getCreatedBy());
        System.out.println("findMember updatedBy = " + findMember.getLastModifiedBy());
    }

}