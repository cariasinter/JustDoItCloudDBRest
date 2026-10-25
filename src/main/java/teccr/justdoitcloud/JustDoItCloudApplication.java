package teccr.justdoitcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import teccr.justdoitcloud.interceptor.AuthenticationInterceptor;
import teccr.justdoitcloud.interceptor.TimingInterceptor;

@SpringBootApplication
public class JustDoItCloudApplication implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final TimingInterceptor timingInterceptor;

    public JustDoItCloudApplication(AuthenticationInterceptor authenticationInterceptor, TimingInterceptor timingInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.timingInterceptor = timingInterceptor;
    }

    public static void main(String[] args) {
        SpringApplication.run(JustDoItCloudApplication.class, args);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/user/home").setViewName("userhome");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(timingInterceptor)
                .addPathPatterns("/**");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

}
