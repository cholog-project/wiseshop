package cholog.wiseshop.common.config;

import cholog.wiseshop.common.auth.AuthArgumentResolver;
import cholog.wiseshop.db.member.MemberRepository;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final MemberRepository memberRepository;

    public WebMvcConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver(memberRepository));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:9000",
                "http://localhost:12000",
                "http://localhost:12001",
                "http://localhost:12002",
                "http://localhost:12003",
                "http://localhost:12004",
                "http://localhost:12005",
                "http://localhost:12006",
                "http://localhost:12007",
                "http://localhost:12008",
                "http://localhost:12009",
                "http://localhost:16001",
                "https://wiseshop.kro.kr"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowCredentials(true);
    }
}
