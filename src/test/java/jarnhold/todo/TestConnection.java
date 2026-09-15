package jarnhold.todo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayDeque;
import java.util.Deque;
import lombok.experimental.Delegate;

public class TestConnection implements Connection {

    @Delegate
    private final Connection delegate;
    private final Deque<Savepoint> savepoints = new ArrayDeque<>();

    public TestConnection(final Connection delegate) throws SQLException {
        this.delegate = delegate;
        this.delegate.setAutoCommit(false);
    }

    public synchronized void rollbackAll() throws SQLException {
        this.delegate.rollback();
        this.savepoints.clear();
    }

    @Override
    public synchronized void close() {
        // close must be a no-op to keep the physical connection open during test execution
    }

    @Override
    public synchronized void commit() throws SQLException {
        final Savepoint savepoint = this.delegate.setSavepoint();
        this.savepoints.addLast(savepoint);
    }

    @Override
    public synchronized void rollback() throws SQLException {
        final Savepoint savepoint = this.savepoints.removeLast();
        this.delegate.rollback(savepoint);
    }
}
