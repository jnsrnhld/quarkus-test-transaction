package jarnhold.todo;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@ApplicationScoped
@RequiredArgsConstructor
public class JdbiProducer {

    private final AgroalDataSource dataSource;

    @Produces
    @ApplicationScoped
    public Jdbi jdbi() {
        final Jdbi jdbi = Jdbi.create(this.dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }
}
