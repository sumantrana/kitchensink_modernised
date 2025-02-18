package org.jboss.as.quickstarts.kitchensink.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findMemberByEmail(String email);

    List<Member> findAllByOrderByName();
}
