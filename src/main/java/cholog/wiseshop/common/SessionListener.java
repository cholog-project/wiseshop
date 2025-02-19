package cholog.wiseshop.common;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    private static final Logger log = LoggerFactory.getLogger("GlobalExceptionHandler");
    private static final AtomicInteger activeSessions = new AtomicInteger(0);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int currentSessions = activeSessions.incrementAndGet();
        log.info("세션이 생성되었습니다. 세션 ID: {}", se.getSession().getId());

        if (currentSessions % 5000 == 0) {
            log.warn("세션 수 {}개 초과", currentSessions);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("세션이 만료되거나 삭제되었습니다. 세션 ID: {}", se.getSession().getId());
    }
}
