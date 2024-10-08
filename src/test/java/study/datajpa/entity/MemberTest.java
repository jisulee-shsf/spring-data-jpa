package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired MemberRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        System.out.println("==========");

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.print("member = " + member);
            System.out.println(" -> member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    public void JpaEventBasicEntity() throws Exception {
        Member member = new Member("memberA");
        memberRepository.save(member); //@PrePersist

        Thread.sleep(100);
        member.setUsername("memberB");

        em.flush(); //@PreUpdate
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
//        System.out.println("findMember.getCreateDate() = " + findMember.getCreateDate());
//        System.out.println("findMember.getUpdateDate() = " + findMember.getUpdateDate());
    }

    @Test
    public void EventBasicEntity() throws Exception {
        Member member = new Member("memberA");
        memberRepository.save(member);

        Thread.sleep(100);
        member.setUsername("memberB");

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println("findMember.getCreateDate() = " + findMember.getCreatedDate());
        System.out.println("findMember.getUpdateDate() = " + findMember.getLastModifiedDate());
        System.out.println("findMember.getCreatedBy() = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
    }
}
