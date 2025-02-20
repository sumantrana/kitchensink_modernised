package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.member.Member;
import org.jboss.as.quickstarts.kitchensink.member.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigInteger;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class MemberServiceTests {

    @Mock
    MemberRepository memberRepository;

    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    void findAllOrderedByName_WhenInvoked_callsFindAllByOrderByNameOfRepository(){

        MemberService memberService = new MemberService(memberRepository);
        memberService.findAllOrderedByName();

        verify(memberRepository, times(1)).findAllByOrderByName();
    }

    @Test
    void findById_WhenInvoked_callsFindByIdOfRepository(){

        MemberService memberService = new MemberService(memberRepository);
        memberService.findById(BigInteger.ONE);

        verify(memberRepository, times(1)).findById(BigInteger.ONE);
    }

    @Test
    void findByEmail_WhenInvoked_callsFindMemberByEmailOfRepository(){

        MemberService memberService = new MemberService(memberRepository);
        memberService.findByEmail("sr@gmail.com");

        verify(memberRepository, times(1)).findMemberByEmail("sr@gmail.com");
    }

    @Test
    void register_WhenInvoked_callsSaveOfRepository() throws Exception {

        Member member = new Member();
        member.setName("John Smith");
        member.setEmail("john.smith@mailinator.com");
        member.setPhoneNumber("2125551212");

        MemberService memberService = new MemberService(memberRepository);
        memberService.register(member);

        verify(memberRepository, times(1)).save(member);
    }
}
