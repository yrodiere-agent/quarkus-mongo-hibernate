package io.quarkiverse.mongohibernate.runtime;

import java.sql.Connection;
import java.sql.SQLException;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.jboss.tm.LastResource;

final class MongoLocalXAResource implements XAResource, LastResource {

    private final Connection connection;
    private Xid currentXid;

    MongoLocalXAResource(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start(Xid xid, int flags) throws XAException {
        if (currentXid == null) {
            if (flags != TMNOFLAGS) {
                throw new XAException(XAException.XAER_INVAL);
            }
            currentXid = xid;
            try {
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw newXAException(XAException.XAER_RMERR, e);
            }
        } else if (flags == TMJOIN || flags == TMRESUME) {
            // Already associated with this branch; nothing to do.
        } else {
            throw new XAException(XAException.XAER_DUPID);
        }
    }

    @Override
    public void end(Xid xid, int flags) throws XAException {
        checkXid(xid);
    }

    @Override
    public int prepare(Xid xid) throws XAException {
        checkXid(xid);
        return XA_OK;
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        checkXid(xid);
        currentXid = null;
        try {
            connection.commit();
        } catch (SQLException e) {
            throw newXAException(onePhase ? XAException.XA_RBROLLBACK : XAException.XAER_RMERR, e);
        } finally {
            close();
        }
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        checkXid(xid);
        currentXid = null;
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw newXAException(XAException.XAER_RMERR, e);
        } finally {
            close();
        }
    }

    @Override
    public void forget(Xid xid) throws XAException {
        throw new XAException(XAException.XAER_NOTA);
    }

    @Override
    public Xid[] recover(int flag) throws XAException {
        return new Xid[0];
    }

    @Override
    public boolean isSameRM(XAResource xaResource) {
        return this == xaResource;
    }

    @Override
    public int getTransactionTimeout() {
        return 0;
    }

    @Override
    public boolean setTransactionTimeout(int seconds) {
        return false;
    }

    private void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            // Closing a ClientSession should not fail under normal circumstances
        }
    }

    private void checkXid(Xid xid) throws XAException {
        if (currentXid == null || !currentXid.equals(xid)) {
            throw new XAException(XAException.XAER_NOTA);
        }
    }

    private static XAException newXAException(int errorCode, Throwable cause) {
        XAException xaException = new XAException(errorCode);
        xaException.initCause(cause);
        return xaException;
    }
}
