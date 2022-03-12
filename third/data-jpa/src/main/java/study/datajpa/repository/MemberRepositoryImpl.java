package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.action.internal.EntityAction;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    /**
     *   custom한 repository기능을 구현하고 싶을 때
     *   1. interface를 선언 및 구현한다.  (ex : MemberRepositoryCustom)
     *   2. interface를 상속하는 구현체를 만든다. (ex : MemberRepositoryImpl)
     *   3. 기준이 되는 interface에 interface를 상속해준다. (ex : MemberRepository 에 extends MemberRepositoryCustom)
     *   4. 실행하면 이제 우리가 만들어놓은 것을 사용 가능.
     *   5. 주의점 !!!
     *      - 상속할 것의 이름 뒤에 xxxImpl형식으로 class명을 정해줘야 한다. 관례 따라 쓰는게 편함.
     *   언제쓸까?
     *      - 복잡한거 짤 때(MemberRepository에서 해결리 불가능할때)
     *
     *   구조적으로 분리하는 것 등등 복합적으로 고려해야 한다.
     */

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
