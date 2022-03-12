package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    //spring data jpa가 스스로 구현체를 생성해서 DI함.
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember(){
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 10);
        assertThat(result.get(0).getUsername()).isEqualTo(m2.getUsername());
        assertThat(result.get(0).getAge()).isEqualTo(m2.getAge());
    }

    @Test
    public void findHelloBy(){
        List<Member> all = memberRepository.findHelloBy();
        all.stream().forEach(System.out::println);
        List<Member> all2 = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery(){
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> member1 = memberRepository.findByUsername("member1");

        assertThat(member1.get(0)).isEqualTo(m1);

    }

    @Test
    public void testQuery(){
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> member1 = memberRepository.findUser("member1", 10);
        assertThat(member1.get(0)).isEqualTo(m1);

    }

    @Test
    public void findUsernameTest() {
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findUsernameDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("member1", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> usernameList = memberRepository.findMemberDto();
        for (MemberDto memberDto : usernameList) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findUsernames(){
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("member1", "member2"));
        System.out.println("==========================");
        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType(){
        Member m1 = new Member("member1", 10);
        Member m2 = new Member("member2", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> member1 = memberRepository.findListByUsername("member1");
        Member member1_Member = memberRepository.findMemberByUsername("member1");
        Optional<Member> member_optional = memberRepository.findOptionalByUsername("member1");

    }

    @Test
    public void paging(){

        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        Pageable pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        long totalElements = page.getTotalElements();

        //then
        List<Member> content = page.getContent();
        content.stream().map(member -> "member = " + member).forEach(System.out::println);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

}