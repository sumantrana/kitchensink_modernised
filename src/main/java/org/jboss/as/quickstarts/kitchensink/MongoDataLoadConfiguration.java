package org.jboss.as.quickstarts.kitchensink;

import com.mongodb.client.MongoClient;
import org.jboss.as.quickstarts.kitchensink.member.Member;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Configuration
public class MongoDataLoadConfiguration {

    @Bean
    ApplicationRunner mongoApplicationRunner(MongoTemplate mongoTemplate, MongoClient mongo){
        return args -> {

            Query query = new Query();
            query.addCriteria(Criteria.where("email").is("john.smith@mailinator.com"));
            List<Member> memberList =  mongoTemplate.find(query, Member.class);

            if ( memberList.isEmpty() ) {
                Member member = new Member();
                member.setName("John Smith");
                member.setEmail("john.smith@mailinator.com");
                member.setPhoneNumber("2125551212");
                mongoTemplate.insert(member, "member");
            }
        };
    }
}
