package org.jboss.as.quickstarts.kitchensink.member;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, BigInteger> {

    Optional<Member> findMemberByEmail(String email);

    List<Member> findAllByOrderByName();
}
