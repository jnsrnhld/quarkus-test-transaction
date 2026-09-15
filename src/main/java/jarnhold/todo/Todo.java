package jarnhold.todo;

import java.time.Instant;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

public record Todo(
    Long id,
    String title,
    String description,
    Boolean completed,
    @ColumnName("created_at") Instant createdAt
) {}
