package org.acme.todo;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InjectableBean;
import io.quarkus.narayana.jta.runtime.NotifyingTransactionManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.transaction.TransactionManager;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

/**
 * DOES NOT WORK: TransactionalInterceptorBase injects a {@link TransactionManager}, but then casts to
 * ((NotifyingTransactionManager) transactionManager), which is not public.
 */
@Priority(1)
@Alternative
@ApplicationScoped
@RequiredArgsConstructor
public class TestTransactionManager implements TransactionManager {

    private final TestConnectionPool connectionPool;
    @Delegate
    private TransactionManager delegate = null;

    @PostConstruct
    void initializeDelegate() {
        final Set<Bean<?>> tmBeans = Arc.container().beanManager()
                .getBeans(TransactionManager.class, Default.Literal.INSTANCE);
        this.delegate = tmBeans.stream()
                .filter(bean -> !bean.isAlternative())
                .map(TestTransactionManager::getBeanReference)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No real TransactionManager found"));
    }

    @Override
    @SneakyThrows
    public void commit() throws SecurityException, IllegalStateException {
        this.connectionPool.getRealConnection().commit();
    }

    @SuppressWarnings("unchecked")
    private static TransactionManager getBeanReference(final Bean<?> bean) {
        return Arc.container().instance((InjectableBean<TransactionManager>) bean).get();
    }
}
