package jarnhold.todo;

import io.agroal.api.AgroalDataSource;
import io.quarkus.arc.Arc;
import io.quarkus.arc.InjectableBean;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import lombok.experimental.Delegate;

@Priority(1)
@Alternative
@Singleton
public class TestConnectionPool implements AgroalDataSource {

    @Delegate
    private AgroalDataSource delegate = null;
    private Connection connection = null;

    @PostConstruct
    void initializeDelegate() {
        final Set<Bean<?>> dataSourceBeans = Arc.container().beanManager()
                .getBeans(AgroalDataSource.class, Default.Literal.INSTANCE);
        this.delegate = dataSourceBeans.stream()
                .filter(bean -> !bean.isAlternative())
                .map(TestConnectionPool::getBeanReference)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No real AgroalDataSource found"));
    }

    @SuppressWarnings("unchecked")
    private static AgroalDataSource getBeanReference(final Bean<?> bean) {
        return Arc.container().instance((InjectableBean<AgroalDataSource>) bean).get();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new TestConnection(this.doGet());
    }

    public Connection getRealConnection() throws SQLException {
        return this.doGet();
    }

    private Connection doGet() throws SQLException {
        if (this.connection == null) {
            this.connection = this.delegate.getConnection();
            this.connection.setAutoCommit(false);
        }
        return this.connection;
    }
}
