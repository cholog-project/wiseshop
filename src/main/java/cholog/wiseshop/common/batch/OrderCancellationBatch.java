package cholog.wiseshop.common.batch;

import cholog.wiseshop.db.order.Order;
import cholog.wiseshop.db.order.OrderState;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class OrderCancellationBatch {

    private static final Logger log = LoggerFactory.getLogger(OrderCancellationBatch.class);
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public OrderCancellationBatch(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        DataSource dataSource
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job orderCancellationJob() {
        return new JobBuilder("orderCancellationJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(orderCancellationStep())
            .build();
    }

    @Bean
    public Step orderCancellationStep() {
        return new StepBuilder("orderCancellationStep", jobRepository)
            .<Order, Order>chunk(10000, transactionManager)
            .reader(orderItemReader(null))
            .processor(orderItemProcessor())
            .writer(orderBatchUpdateItemWriter())
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Order> orderItemReader(
        @Value("#{jobParameters['productId']}") Long productId
    ) {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT id");
        queryProvider.setFromClause("FROM `order`");
        queryProvider.setWhereClause("WHERE product_id = :productId AND state = :state");
        queryProvider.setSortKey("id");

        JdbcPagingItemReader<Order> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(10000);
        reader.setRowMapper(new OrderRowMapper());

        reader.setParameterValues(Map.of(
            "productId", productId,
            "state", OrderState.SUCCESS.toString()
        ));

        try {
            reader.setQueryProvider(queryProvider.getObject());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set query provider", e);
        }

        return reader;
    }

    @Bean
    public ItemProcessor<Order, Order> orderItemProcessor() {
        return order -> {
            order.setState(OrderState.FAILURE);
            order.setModifiedDate(LocalDateTime.now());
            return order;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Order> orderBatchUpdateItemWriter() {
        JdbcBatchItemWriter<Order> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);

        writer.setSql(
            "UPDATE `order` SET state = :state, modified_date = :modified_date WHERE id = :id");
        writer.setItemSqlParameterSourceProvider(order -> {
            MapSqlParameterSource paramSource = new MapSqlParameterSource();
            paramSource.addValue("id", order.getId());
            paramSource.addValue("state", order.getState().toString());
            paramSource.addValue("modified_date", order.getModifiedDate());
            return paramSource;
        });

        OrderCancellationBatch.log.warn("모든 주문 취소를 완료했습니다.");

        return writer;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

    static class OrderRowMapper implements RowMapper<Order> {

        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            return order;
        }
    }
}
