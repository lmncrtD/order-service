package lmncrt.analyticsservice.config;

import com.zaxxer.hikari.HikariDataSource;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(
        defaultLockAtLeastFor = "${lockAtLeast}",
        defaultLockAtMostFor = "${lockAtMostFor}"
)
public class ShedLockConfig {

    @Bean
    @Primary
    public DataSource clickHouseDataSource(
            @Value("${spring.datasource.url}")  String url,
            @Value("${spring.datasource.username}") String user,
            @Value("${spring.datasource.password}") String pass
    ) {
        HikariDataSource hikariConnection = new HikariDataSource();
        hikariConnection.setJdbcUrl(url);
        hikariConnection.setUsername(user);
        hikariConnection.setPassword(pass);

        return hikariConnection;

    }

    @Bean
    public DataSource postgresDataSource(
            @Value("${analytics.shedlock.datasource.url}")  String url,
            @Value("${analytics.shedlock.datasource.username}") String user,
            @Value("${analytics.shedlock.datasource.password}") String pass
    ) {

        HikariDataSource hikariConnection = new HikariDataSource();
        hikariConnection.setJdbcUrl(url);
        hikariConnection.setUsername(user);
        hikariConnection.setPassword(pass);

        return hikariConnection;
    }

    @Bean
    public LockProvider lockProvider(
            @Qualifier("postgresDataSource") DataSource dataSource
    ) {

        var jdbcTemplate = new JdbcTemplate(dataSource);

        var configuration = JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(jdbcTemplate)
                .usingDbTime()
                .build();

        return new JdbcTemplateLockProvider(configuration);
    }

}
