package jpql;

import javax.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpashop");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
//            String query = "select m from Member m left join m.team t on t.name = 'teamA'";
//            String query = "select m from Member m left join Team t on m.username = t.name";
//            List<Member> result = em.createQuery(query, Member.class)
//                    .getResultList();

            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

//            String query = "select m from Member m join fetch m.team";
//            List<Member> resultList = em.createQuery(query, Member.class).getResultList();
//            for (Member member : resultList) {
//                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
//            }

            String query = "select t From Team t join fetch t.members";
            List<Team> result = em.createQuery(query, Team.class).getResultList();
            for (Team team : result) {
                System.out.println("team.getName() + \", \" + team.getMembers().size() = " + team.getName() + ", " + team.getMembers().size()); 
                for(Member m : team.getMembers()){
                    System.out.println("m = " + m);
                }
            }
            
//            List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
//                    .setFirstResult(0)
//                    .setMaxResults(10)
//                    .getResultList();
//
//            System.out.println("resultList.size = " + resultList.size());
//            for (Member member1 : resultList) {
//                System.out.println("member = " + member1);
//            }

//            List<MemberDto> resultList = em.createQuery("select new jpql.MemberDto(m.username, m.age) from Member m", MemberDto.class).getResultList();
//
//            MemberDto memberDto = resultList.get(0);
//            System.out.println("memberDto = " + memberDto.getUsername());
//            System.out.println("memberDto = " + memberDto.getAge());

            em.flush();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }
}
