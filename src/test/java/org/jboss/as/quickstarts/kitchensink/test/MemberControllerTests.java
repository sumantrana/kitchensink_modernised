package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.member.internal.Member;
import org.jboss.as.quickstarts.kitchensink.member.MemberController;
import org.jboss.as.quickstarts.kitchensink.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberService memberService;


    @Test
    void get_whenInvoked_returnsViewWithAllMembers() throws Exception{

        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("members"))
                .andExpect(model().attributeExists("member"));
    }

    @Test
    void getAll_whenInvoked_returnsAllMembers() throws Exception{

        Member member = new Member();
        member.setName("John Smith");
        member.setEmail("john.smith@mailinator.com");
        member.setPhoneNumber("2125551212");

        given(memberService.findAllOrderedByName()).willReturn(List.of(member));

        this.mockMvc
                .perform(get("/rest/members").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[0].email").value("john.smith@mailinator.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("2125551212"));
    }

    @Test
    void get_whenInvokedWithGivenId_returnsMemberWithThatId() throws Exception{

        Member member = new Member();
        member.setName("John Smith");
        member.setEmail("john.smith@mailinator.com");
        member.setPhoneNumber("2125551212");

        given(memberService.findById(any())).willReturn(Optional.of(member));

        this.mockMvc
                .perform(get("/rest/members/0").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("John Smith"))
                .andExpect(jsonPath("email").value("john.smith@mailinator.com"))
                .andExpect(jsonPath("phoneNumber").value("2125551212"));
    }

    @Test
    void post_whenInvokedWithValidInput_redirectsToContextRoot() throws Exception{

        this.mockMvc
                .perform(post("/rest/members")
                        .param("name", "John Smith")
                        .param("email", "john.smith@mailinator.com")
                        .param("phoneNumber", "2125551212"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists("successMessage"));

    }

    @Test
    void post_whenInvokedWithInValidInput_redirectsToContextRootWithErrors() throws Exception{

        this.mockMvc
                .perform(post("/rest/members"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists("validationFailures"));

    }

    @Test
    void post_whenExceptionRaisedInternally_redirectsToContextRootWithErrors() throws Exception{

        doThrow(new Exception("Error")).when(memberService).register(any());

        this.mockMvc
                .perform(post("/rest/members")
                        .param("name", "John Smith")
                        .param("email", "john.smith@mailinator.com")
                        .param("phoneNumber", "2125551212"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attributeExists("errorMessage"));

    }

}