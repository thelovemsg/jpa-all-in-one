package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    //spring data jpa가 스스로 구현체를 생성해서 DI함.
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired
    EntityManager em;

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

    @Test
    public void bulkUpdateTest(){
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(10);

        em.flush(); // 변경사항 반영
        em.clear(); // 영속성 클리어!
        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println("member = " + member);
        // bulk연산시에는 영속성의 내용 상관없이 바로 db에 전송함 => 서로 값이 다름
        //=> 그러므로 벌크업로드 뒤에는 영속성을 날려버려야 한다.
        //혹은 해당 메서드의 @Modifying을 clearAuthmatically = true로 변경한다.

        assertThat(resultCount).isEqualTo(5);

    }

    //@EntityGraph 기능
    @Test
    public void findMemberLazy(){
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
//        List<Member> members = memberRepository.findMemberFetchJoin();
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        members.stream().forEach(m -> {
            System.out.println("m.getUsername() = " + m.getUsername());
            System.out.println("m.getTeam().getClass() = " + m.getTeam().getClass());
            // 일단 멤버만 끌고오는데 team은?
            // null로 둘수가 없어서 우선 lazy라서 proxy라는 가짜 객체를 생성해서 넣어둠
            System.out.println("m.getTeam().getName() = " + m.getTeam().getName());
            //실제로 조회할 때 이제 조회를 해옴. => N + 1 문제 발생! 적은 쿼리로 한번에 다 가져와야 한다!
        });
    }

    @Test
    public void queryHint(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        memberRepository.save(member1);
        em.flush(); // 결과를 db에 공유
        em.clear();

        //when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
        em.flush();
        // update 쿼리 나감 => dirty checking
        // 치명젹인 약점 => 원래 객체가 뭔지 알아야 함 => 메모리를 잡아먹음 => 비용이 발생
        // 나는 그냥 db에서 조회만 하고 끝내고 싶다면? => 최적화 방법이 있음. hints에 hint를 주면 된다.

        // => hints 기능을 이용하기
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // update query가 이제 안날라감

        em.flush(); // 변경 안됌 이제
        //근데 이것이 성능이 크게 잡아먹는지는... 잘 모름 => 일일이 다 넣어야 하는 것은 애매. 중요한것에 집중하고 이점이 있어야 사용한다.
    }

    @Test
    public void testLock(){
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        memberRepository.save(member1);
        em.flush(); // 결과를 db에 공유
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

        em.flush();
    }

    //QueryDsl의 경우 custom을 많이 사용한다고 한다.
    //복잡해진 것은 queryDsl쓰면 좋음. !!!!!#!@#!@#!@#
    //
    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample() {
        //given

        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        memberRepository.findByUsername("m1");
        //Probe
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");

    }

    @Test
    public void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
//        List<NestedClosedProjections> m11 = memberRepository.findProjectionByUsername("m1", NestedClosedProjections.class);
//        List<NestedClosedProjections> result = m11;
//        for (NestedClosedProjections nestedClosedProjections : result) {
//            System.out.println("nestedClosedProjections = " + nestedClosedProjections);
//        }

    }

    @Test
    public void nativeQuery(){
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamname());
        }
    }
}