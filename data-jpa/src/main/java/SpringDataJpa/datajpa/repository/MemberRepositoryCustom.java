package SpringDataJpa.datajpa.repository;

import SpringDataJpa.datajpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
