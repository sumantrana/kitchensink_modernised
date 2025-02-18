package org.jboss.as.quickstarts.kitchensink;

import jakarta.servlet.DispatcherType;
import org.apache.myfaces.webapp.MyFacesServlet;
import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.EnumSet;

@SpringBootApplication
public class SpringBootApp extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }

    @Bean
    public ServletRegistrationBean<MyFacesServlet> servletRegistrationBean() {
        MyFacesServlet servlet = new MyFacesServlet();
        return new ServletRegistrationBean(servlet, "*.jsf");
    }

    @Bean
    public FilterRegistrationBean rewriteFilter() {
        FilterRegistrationBean rwFilter = new FilterRegistrationBean(new RewriteFilter());
        rwFilter.setDispatcherTypes(EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST,
                DispatcherType.ASYNC, DispatcherType.ERROR));
        rwFilter.addUrlPatterns("/*");
        return rwFilter;
    }

}
