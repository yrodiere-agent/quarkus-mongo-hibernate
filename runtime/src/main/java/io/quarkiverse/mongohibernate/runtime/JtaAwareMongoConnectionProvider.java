package io.quarkiverse.mongohibernate.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.InjectService;
import org.hibernate.service.spi.Stoppable;

import com.mongodb.hibernate.internal.service.StandardServiceRegistryScopedState;

public final class JtaAwareMongoConnectionProvider implements ConnectionProvider, Stoppable {

    private static final long serialVersionUID = 1L;

    private final com.mongodb.hibernate.internal.jdbc.MongoConnectionProvider delegate = new com.mongodb.hibernate.internal.jdbc.MongoConnectionProvider();

    private volatile TransactionManager transactionManager;

    @InjectService
    public void injectStandardServiceRegistryScopedState(StandardServiceRegistryScopedState state) {
        delegate.injectStandardServiceRegistryScopedState(state);
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = delegate.getConnection();
        TransactionManager tm = transactionManager();
        if (tm == null) {
            return connection;
        }
        try {
            Transaction tx = tm.getTransaction();
            if (tx != null && tx.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE) {
                if (!tx.enlistResource(new MongoLocalXAResource(connection))) {
                    connection.close();
                    throw new SQLException(
                            "Failed to enlist MongoDB connection in JTA transaction."
                                    + " Only one non-XA resource can participate in a JTA transaction;"
                                    + " you cannot use multiple MongoDB persistence units"
                                    + " (or a MongoDB persistence unit and a non-XA datasource)"
                                    + " in the same transaction.");
                }
                return suppressClose(connection);
            }
        } catch (SystemException | RollbackException e) {
            connection.close();
            throw new SQLException("Failed to enlist MongoDB connection in JTA transaction", e);
        }
        return connection;
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return delegate.supportsAggressiveRelease();
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return delegate.isUnwrappableAs(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return delegate.unwrap(unwrapType);
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    private static Connection suppressClose(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] { Connection.class },
                new CloseIgnoringInvocationHandler(connection));
    }

    private record CloseIgnoringInvocationHandler(Connection delegate) implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("close".equals(method.getName())) {
                return null;
            }
            try {
                return method.invoke(delegate, args);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private TransactionManager transactionManager() {
        TransactionManager tm = this.transactionManager;
        if (tm == null) {
            try {
                tm = com.arjuna.ats.jta.TransactionManager.transactionManager();
                this.transactionManager = tm;
            } catch (Exception e) {
                return null;
            }
        }
        return tm;
    }
}
