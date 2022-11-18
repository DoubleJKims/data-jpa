package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/v1/{id}")
    public String findMemberV1(@PathVariable("id") Long id) {
        Optional<Member> byId = memberRepository.findById(id);
        Member findMember = byId.get();
        return findMember.getUsername();
    }

    /**
     * 도메인 클래스 컨버터 예시 (조회용으로만 사용 권장)
     * @param member
     * @return
     */
    @GetMapping("/members/v2/{id}")
    public String findMemberV2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * 페이징과 정렬
     * http://localhost:8080/members?page=1&size=3&sort=id,desc&sort=username,desc
     * 글로벌 설정(application.yaml)과 로컬 설정(@PageableDefault)을 할 수 있음
     * @param pageable
     * @return
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable)
                //.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
                //.map(member -> new MemberDto(member));
                .map(MemberDto::new);
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
