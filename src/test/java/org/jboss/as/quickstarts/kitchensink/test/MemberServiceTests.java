package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.member.internal.Member;
import org.jboss.as.quickstarts.kitchensink.member.internal.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.event.ApplicationEvents;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class MemberServiceTests {

    @Mock
    MemberRepository memberRepository;

    @Mock
    ApplicationEventPublisher events;

    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    void findAllOrderedByName_WhenInvoked_callsFindAllByOrderByNameOfRepository(){

        MemberService memberService = new MemberService(memberRepository, events);
        memberService.findAllOrderedByName();

        verify(memberRepository, times(1)).findAllByOrderByName();
    }

    @Test
    void findById_WhenInvoked_callsFindByIdOfRepository(){

        MemberService memberService = new MemberService(memberRepository, events);
        memberService.findById(1L);

        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void findByEmail_WhenInvoked_callsFindMemberByEmailOfRepository(){

        MemberService memberService = new MemberService(memberRepository, events);
        memberService.findByEmail("sr@gmail.com");

        verify(memberRepository, times(1)).findMemberByEmail("sr@gmail.com");
    }

    @Test
    void register_WhenInvoked_callsSaveOfRepository() throws Exception {

        Member member = new Member();
        member.setName("John Smith");
        member.setEmail("john.smith@mailinator.com");
        member.setPhoneNumber("2125551212");

        MemberService memberService = new MemberService(memberRepository, events);
        memberService.register(member);

        verify(memberRepository, times(1)).save(member);
    }
}
