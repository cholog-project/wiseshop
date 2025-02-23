package cholog.wiseshop.common.batch;

import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderState;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class OrderCancelationBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public OrderCancelationBatch(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DataSource dataSource
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    /**
     * 주문 취소 배치 Job
     */
    @Bean
    public Job orderCancellationJob() {
        return new JobBuilder("orderCancellationJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(orderCancellationStep())
            .build();
    }

    /**
     * 주문 취소 Step (읽기 → 처리 → 쓰기)
     */
    @Bean
    public Step orderCancellationStep() {
        return new StepBuilder("orderCancellationStep", jobRepository)
            .<Order, Order>chunk(1000, transactionManager)
            .reader(orderItemReader(null))
            .processor(orderItemProcessor())
            .writer(orderBatchUpdateItemWriter())
            .build();
    }

    /**
     * 주문 조회 Reader (JDBC 페이징)
     * - ID만 조회하여 성능 최적화
     */
    @Bean
    @StepScope
    public JdbcPagingItemReader<Order> orderItemReader(
        @Value("#{jobParameters['productId']}") Long productId
    ) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT id"); // ID만 조회
        queryProvider.setFromClause("FROM `order`");
        queryProvider.setWhereClause("WHERE product_id = :productId AND state = :state");
        queryProvider.setSortKey("id"); // 페이징 정렬 기준

        JdbcPagingItemReader<Order> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(1000);
        reader.setRowMapper(new OrderRowMapper()); // ID만 매핑

        reader.setParameterValues(Map.of(
            "productId", productId,
            "state", OrderState.SUCCESS.name() // Enum을 String으로 변환
        ));

        try {
            reader.setQueryProvider(queryProvider.getObject());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set query provider", e);
        }

        return reader;
    }

    /**
     * 주문 상태 변경 Processor
     * - 상태를 FAILED로 변경
     * - 수정 날짜를 현재 시간으로 설정
     */
    @Bean
    public ItemProcessor<Order, Order> orderItemProcessor() {
        return order -> {
            order.setState(OrderState.FAILURE);
            order.setModifiedDate(LocalDateTime.now());
            return order;
        };
    }

    /**
     * 주문 상태 업데이트 Writer (JDBC Batch Update)
     * - ID 기준으로 상태 및 수정 날짜 변경
     */
    @Bean
    public JdbcBatchItemWriter<Order> orderBatchUpdateItemWriter() {
        JdbcBatchItemWriter<Order> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);

        writer.setSql("UPDATE `order` SET state = :state, modified_date = :modified_date WHERE id = :id");
        writer.setItemSqlParameterSourceProvider(order -> {
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            paramSource.addValue("id", order.getId());
            paramSource.addValue("state", order.getState().toString());
            paramSource.addValue("modified_date", order.getModifiedDate());
            return paramSource;
        });

        System.out.println("완료했음 확인바람");

        return writer;
    }

    /**
     * 주문 데이터 매핑 RowMapper
     * - ID만 조회하여 매핑
     */
    static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("id")); // ID만 매핑
            return order;
        }
    }
}
