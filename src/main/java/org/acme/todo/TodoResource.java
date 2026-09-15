package org.acme.todo;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Jdbi;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class TodoResource {

    private final Jdbi jdbi;

    @GET
    public List<Todo> getAll() {
        return this.jdbi.onDemand(TodoDao.class).findAll();
    }

    @GET
    @Path("/{id}")
    public Todo getById(@PathParam("id") final Long id) {
        return this.jdbi.onDemand(TodoDao.class)
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Todo not found with id: " + id));
    }

    @POST
    @Transactional
    public Response create(final Todo todo) {
        final Long generatedId = this.jdbi.onDemand(TodoDao.class).insert(todo);
        final Todo created = this.jdbi.onDemand(TodoDao.class)
            .findById(generatedId)
            .orElseThrow(() -> new IllegalStateException("Failed to retrieve created todo"));
        return Response.created(URI.create("/todos/" + generatedId)).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Todo update(@PathParam("id") final Long id, final Todo todo) {
        final Todo updatedTodo = new Todo(id, todo.title(), todo.description(), todo.completed(), todo.createdAt());
        final boolean updated = this.jdbi.onDemand(TodoDao.class).update(updatedTodo);
        if (!updated) {
            throw new NotFoundException("Todo not found with id: " + id);
        }
        return this.jdbi.onDemand(TodoDao.class)
            .findById(id)
            .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated todo"));
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") final Long id) {
        final boolean deleted = this.jdbi.onDemand(TodoDao.class).deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Todo not found with id: " + id);
        }
        return Response.noContent().build();
    }
}
