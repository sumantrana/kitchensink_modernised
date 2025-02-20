package org.jboss.as.quickstarts.kitchensink.member;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

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
    public String newMemberForm(@ModelAttribute Member member, Model model){
        model.addAttribute("members", memberService.findAllOrderedByName());
        if ( model.containsAttribute("validationFailures")){
            Object failures = model.getAttribute("validationFailures");
            model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "member", failures);
        }
        return "index";
    }

    @GetMapping(path = "/rest/members")
    public ResponseEntity<List<Member>> listAllMembers() {
        return new ResponseEntity<>(memberService.findAllOrderedByName(), HttpStatus.OK);
    }

    @GetMapping(path = "/rest/members/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") Long id) {

        Optional<Member> member = memberService.findById(id);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping(path = "/rest/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createMember(@ModelAttribute Member member, Model model, RedirectAttributes redirectAttributes) {

        try {

            // Validates member using bean validation
            BindingResult bindingResult = validateMember(member);
            if ( bindingResult.hasErrors() ){
                redirectAttributes.addFlashAttribute("validationFailures", bindingResult);
            } else {
                memberService.register(member);
                redirectAttributes.addFlashAttribute("successMessage", "Registered!");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/";
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
                bindingResult.rejectValue("email", "NotUnique", "Email already registered.");
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

        Optional<Member> member = memberService.findByEmail(email);
        return member.isPresent();

    }
}