package com.saisai.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Filter;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class NotDeletedFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * com.saisai.domain 아래의 모든 JpaRepository 메서드에 필터를 적용하는 Advice.
     * findAllBy, findBy 같은 특정 패턴의 메서드에만 적용되도록 설정할 수 있습니다.
     * @param joinPoint 프록시된 메서드에 대한 정보
     * @return 메서드 실행 결과
     * @throws Throwable
     */
    @Around("execution(* com.saisai.domain..repository.*Repository.*(..))")
    public Object applyNotDeletedFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Filter filter = null;
        try {
            filter = entityManager.unwrap(org.hibernate.Session.class).enableFilter("notDeleted");
            filter.setParameter("isDeleted", false);

            return joinPoint.proceed();
        } finally {
            if (filter != null) {
                entityManager.unwrap(org.hibernate.Session.class).disableFilter("notDeleted");
            }
        }
    }

}
