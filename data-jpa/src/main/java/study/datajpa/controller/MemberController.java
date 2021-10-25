package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.jpaRepo.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
        }


    /*도메인 컨버터 사용
    도메인 클래스 컨버터로 엔티티를 파라미터로 받으면,
     이 엔티티는 단순 조회용으로만 사용해야 한다.
     엔티티를 변경해도 DB에 반영되지 않는다
     */
    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    //Pageable 은 인터페이스, 실제는 org.springframework.data.domain.PageRequest 객체 생성
    ///members?page=0&size=3&sort=id,desc&sort=username,desc
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(
            size = 12
            , sort = "username"
            , direction = Sort.Direction.DESC
    ) Pageable pageable) {

        Page<Member> page = memberRepository.findAll(pageable);

        return page.map(MemberDto::new);
    }

    /*페이징 정보가 둘 이상이면 접두사로 구분 @Qualifier 에 접두사명 추가 "{접두사명}_xxx”
    예제: /members?member_page=0&order_page=1
    public String list(
    @Qualifier("member") Pageable memberPageable, @Qualifier("order") Pageable orderPageable,*/

}
