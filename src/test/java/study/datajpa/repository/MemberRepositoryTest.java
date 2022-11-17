package study.datajpa.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;
    @Test
    public void testMember() {

        System.out.println("memberRepository = " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> findMemberById = memberRepository.findById(savedMember.getId());

        if(!findMemberById.isPresent()){
            Member findMember = findMemberById.get();
            assertThat(findMember.getId()).isEqualTo(member.getId());
            assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
            assertThat(findMember).isEqualTo(member);
        }
    }

    @Test
    @DisplayName("basicCRUD")
    void basicCrud() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //변경감지(dirty checking)
        findMember1.changeUsername("user!!!!!");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);


        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long count1 = memberRepository.count();
        assertThat(count1).isEqualTo(0);
    }

    @Test
    @DisplayName("findByUsernameAndAgeGreaterThan")
    void findByUsernameAndAgeGreaterThan() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("findHelloBy")
    void findHelloBy() throws Exception {
        List<Member> helloBy = memberRepository.findHelloBy();
    }

    @Test
    @DisplayName("findTop3HelloBy")
    void findTop3HelloBy() throws Exception {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    @DisplayName("testQuery")
    void testQuery() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(m1);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("findUsernameList")
    void findUsernameList() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }

    }

    @Test
    @DisplayName("findMemberDto")
    void findMemberDto() throws Exception {
        Team team = new Team("teamA");
        teamRepository.save(team);
        
        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        List<MemberDto> memberDto  = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    @DisplayName("findByNames")
    void findByNames() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    @DisplayName("returnTypeTest")
    void returnTypeTest() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        Member m3 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);

        List<Member> ListByUsername = memberRepository.findListByUsername("AAA");
        List<Member> ListByUsername2 = memberRepository.findListByUsername("asdfasdf");
        System.out.println("ListByUsername2 size = " + ListByUsername2.size());

        Member memberByUsername = memberRepository.findMemberByUsername("BBB");
        Member memberByUsername2 = memberRepository.findMemberByUsername("asdfasdf");
        System.out.println("memberByUsername2 = " + memberByUsername2);

        //org.springframework.dao.IncorrectResultSizeDataAccessException: query did not return a unique result: 2;
        Optional<Member> optionalByUsername = memberRepository.findOptionalByUsername("AAA");
        System.out.println("optionalByUsername = " + optionalByUsername);
    }

    @Test
    @DisplayName("pagingAndSlice")
    void pagingAndSlice() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(toMap.getTotalElements()).isEqualTo(5);
        assertThat(toMap.getNumber()).isEqualTo(0);
        assertThat(toMap.getTotalPages()).isEqualTo(2);
        assertThat(toMap.isFirst()).isTrue();
        assertThat(toMap.hasNext()).isTrue();

        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest);

        assertThat(content.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);
        assertThat(slice.isFirst()).isTrue();
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("bulkAgePlus")
    void bulkAgePlus() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("asisResult member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    @DisplayName("findMemberLazy")
    void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> all = memberRepository.findAll();

        for (Member member : all) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //가짜 객체  class study.datajpa.entity.Team$HibernateProxy$cW80nZmh
            System.out.println("member.team = " + member.getTeam().getName());
        }

        List<Member> memberFetchJoin = memberRepository.findMemberFetchJoin();
        for (Member member : memberFetchJoin) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); //진짜 객체  class study.datajpa.entity.Team
            System.out.println("member.team = " + member.getTeam().getName());
        }

        List<Member> memberEntityGraph = memberRepository.findEntityGraphByUsername("member1");
    }

    @Test
    @DisplayName("queryHint")
    void queryHint() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); //영속성컨텍스트(1차 캐시)에 데이터 남아있음
        em.clear(); //영속성컨텍스트 초기화 및 다음 트랜잭션에서 DB 조회
        Long id = member1.getId();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.changeUsername("member2");

        em.flush();
    }

    @Test
    @DisplayName("lock")
    void lock() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush(); //영속성컨텍스트(1차 캐시)에 데이터 남아있음
        em.clear(); //영속성컨텍스트 초기화 및 다음 트랜잭션에서 DB 조회

        //when
        Member findMember = memberRepository.findLockByUsername("member1");
    }
}