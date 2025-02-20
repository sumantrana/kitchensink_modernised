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
package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberIntegrationTests {

    private static final Logger log = Logger.getLogger(MemberIntegrationTests.class.getName());

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    protected URI getHTTPEndpoint() {
        String host = "http://localhost:" + port;
        try {
            //return new URI(host);
            return new URI(host + "/kitchensink/rest/members");
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void register_WithValidParams_RegistersMember() throws Exception {

        String currentMillis = String.valueOf(System.currentTimeMillis());
        Member newMember = new Member(null, "Jane Doe", currentMillis + "-jane@mailinator.com", "2125551234");

        HttpEntity<Member> request = new HttpEntity<>(newMember);
        ResponseEntity<Map> response = testRestTemplate.postForEntity(getHTTPEndpoint(), request, Map.class);

        assertThat(response.getStatusCode().is3xxRedirection()).isTrue();
        assertThat(response.hasBody()).isFalse();

    }

}
