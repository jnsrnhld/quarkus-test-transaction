package jarnhold.todo;

import java.util.List;
import java.util.Optional;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterConstructorMapper(Todo.class)
public interface TodoDao {

    @SqlQuery("SELECT id, title, description, completed, created_at FROM todo ORDER BY id")
    List<Todo> findAll();

    @SqlQuery("SELECT id, title, description, completed, created_at FROM todo WHERE id = :id")
    Optional<Todo> findById(@Bind("id") final Long id);

    @SqlUpdate("INSERT INTO todo (title, description, completed) VALUES (:title, :description, COALESCE(:completed, 0))")
    @GetGeneratedKeys("id")
    Long insert(@BindMethods final Todo todo);

    @SqlUpdate("UPDATE todo SET title = :title, description = :description, completed = :completed WHERE id = :id")
    boolean update(@BindMethods final Todo todo);

    @SqlUpdate("DELETE FROM todo WHERE id = :id")
    boolean deleteById(@Bind("id") final Long id);
}
