package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor{

    List<Member> findByUsernameAndAgeGreaterThan(String username,int age);

    List<Member> findHelloBy();

    List<Member> findTop3HelloBy();

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); // collection
    Member findMemberByUsername(String username);// single
    Optional<Member> findOptionalByUsername(String username);// single Optional

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying // update쿼리 시에는 반드시 넣어줘야 update인지 인식한다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    //jpa 에서 fetch join 기능 쓸 떄 마다 이렇게 해야해서 굉장히 불편
    // => EntityGragh를 이용해서 해결이 가능하다.
    @Query("select m from Member m join fetch m.team t")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    /**
     * 이것 말고도 직접 entitiy안에 선언도 가능하다.
     * @NamedEntityGraph(
        name ="Member.all",
        attributeNodes = @NamedAttributeNode("team"))
     * */

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    <T> List<T> findProjectionByUsername(@Param("username") String username);

    @Query(value ="select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value ="select m.member_id as id, m.username, t.name as teamName " +
            "from Member m from left join team t",
            countQuery = "select count(*) from Member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
