package cholog.wiseshop.common;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
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

    @PostConstruct
    private void findDatabaseTableNames() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            // Get actual table name if @Table annotation is present
            Table tableAnnotation = entity.getJavaType().getAnnotation(Table.class);
            String tableName = tableAnnotation != null ? tableAnnotation.name() : entity.getName();
            tableNames.add(tableName);
        }
    }

    @Transactional
    public void clear() {
        entityManager.clear();

        try {
            // Disable foreign key checks
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

            for (String tableName : tableNames) {
                // Use truncate for faster cleaning
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            }

            // Re-enable foreign key checks
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

            entityManager.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database: " + e.getMessage(), e);
        }
    }
}
