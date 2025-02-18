/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.member;

import jakarta.persistence.NoResultException;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/kitchensink/rest/members")
public class MemberResourceController {

    private final MemberService memberService;

    private final Validator validator;

    public MemberResourceController(MemberService memberService){
        this.memberService = memberService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> listAllMembers() {
        return memberService.findAllOrderedByName();
    }

    @GetMapping(path = "/{id:[0-9]+}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable("id") long id) {

        Optional<Member> member = memberService.findById(id);
        return member.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    //@PostMapping(value = "/kitchensink/rest/members", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> createMember(@RequestBody Member member) {

        ResponseEntity<Map<String, String>> builder = null;

        try {
            // Validates member using bean validation
            validateMember(member);
            memberService.register(member);

            // Create an "ok" response
            builder = ResponseEntity.ok().build();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            builder = ResponseEntity.status(HttpStatus.CONFLICT).body(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
        }

        return builder;
    }

    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateMember(Member member) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private ResponseEntity<Map<String, String>> createViolationResponse(Set<ConstraintViolation<?>> violations) {

        log.debug("Validation completed. violations found: {}", violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseObj);
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
