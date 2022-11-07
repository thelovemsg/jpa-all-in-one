package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.InstanceOfAssertFactories.PATH;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @Test
//    @Rollback(false)
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("msg");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush();
        assertThat(memberRepository.findOne(savedId)).isEqualTo(member);
    }

    @Test()
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("msg1");

        Member member2 = new Member();
        member2.setName("msg1");

        //when
        memberService.join(member1);
        Assertions.assertThrows(IllegalStateException.class,() -> memberService.join(member2));

    }

}