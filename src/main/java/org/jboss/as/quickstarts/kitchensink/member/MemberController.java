package org.jboss.as.quickstarts.kitchensink.member;

import jakarta.persistence.NoResultException;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Slf4j
@Controller
public class MemberController {

    private final MemberService memberService;

    private final Validator validator;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @GetMapping(path="/")
    public String newMemberForm(Model model){
        model.addAttribute("member", new Member());
        model.addAttribute("members", memberService.findAllOrderedByName());
        return "index";
    }

    @GetMapping(path = "/rest/members")
    public ResponseEntity<List<Member>> listAllMembers() {
        return new ResponseEntity<>(memberService.findAllOrderedByName(), HttpStatus.OK);
    }

    @GetMapping(path = "/rest/members/{id:[0-9]+}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {

        Optional<Member> member = memberService.findById(id);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createMember(@ModelAttribute Member member, Model model) {

        try {

            // Validates member using bean validation
            BindingResult bindingResult = validateMember(member);
            if ( bindingResult.hasErrors() ){
                model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "member", bindingResult);
            } else {
                memberService.register(member);
                model.addAttribute("member", new Member());
                model.addAttribute("successMessage", "Registered!");
            }

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("members", memberService.findAllOrderedByName());
        return "index";
    }

    /**
     * <p>
     * Validates the given Member variable and return BindingResult with mapped Errors
     * </p>
     *
     * @param member Member to be validated
     * @return BindingResult
     */

    private BindingResult validateMember(Member member) {

        // Create a bean validator and check for issues.
        SpringValidatorAdapter springValidator = new SpringValidatorAdapter(validator);
        BindingResult bindingResult = new BeanPropertyBindingResult(member, "member");
        springValidator.validate(member, bindingResult);

        if (! bindingResult.hasErrors() ) {
            if (emailAlreadyExists(member.getEmail())) {
                bindingResult.rejectValue("email", "NotUnique", "Email taken");
            }
        }

        return bindingResult;
    }

    /**
     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email) {
        Member member = null;
        try {
            member = memberService.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }
        return member != null;
    }
}
