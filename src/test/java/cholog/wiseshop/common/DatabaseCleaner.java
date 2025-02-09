package cholog.wiseshop.common;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    private final List<String> tableNames = new ArrayList<>();

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void findDatabaseTableNames() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            tableNames.add(entity.getName());
        }
    }

    private void truncate() {
        try {
            for (String tableName : tableNames) {
                entityManager.createQuery("DELETE FROM " + tableName).executeUpdate();
            }
            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    @Transactional
    public void clear() {
        entityManager.clear();
        truncate();
    }
}
