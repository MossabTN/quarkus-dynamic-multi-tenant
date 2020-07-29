package io.maxilog.config;

import io.agroal.api.AgroalDataSourceListener;
import org.jboss.logging.Logger;

import java.sql.Connection;

final class AgroalEventLoggingListener implements AgroalDataSourceListener {
    private static final Logger log = Logger.getLogger("io.agroal.pool");
    private final String datasourceName;

    public AgroalEventLoggingListener(String name) {
        this.datasourceName = "Datasource '" + name + "'";
    }

    public void beforeConnectionLeak(Connection connection) {
        log.tracev("{0}: Leak test on connection {1}", this.datasourceName, connection);
    }

    public void beforeConnectionReap(Connection connection) {
        log.tracev("{0}: Reap test on connection {1}", this.datasourceName, connection);
    }

    public void beforeConnectionValidation(Connection connection) {
        log.tracev("{0}: Validation test on connection {1}", this.datasourceName, connection);
    }

    public void onConnectionAcquire(Connection connection) {
        log.tracev("{0}: Acquire connection {1}", this.datasourceName, connection);
    }

    public void onConnectionCreation(Connection connection) {
        log.tracev("{0}: Created connection {1}", this.datasourceName, connection);
    }

    public void onConnectionReap(Connection connection) {
        log.tracev("{0}: Closing idle connection {1}", this.datasourceName, connection);
    }

    public void onConnectionReturn(Connection connection) {
        log.tracev("{0}: Returning connection {1}", this.datasourceName, connection);
    }

    public void onConnectionDestroy(Connection connection) {
        log.tracev("{0}: Destroyed connection {1}", this.datasourceName, connection);
    }

    public void onWarning(String warning) {
        log.warnv("{0}: {1}", this.datasourceName, warning);
    }

    public void onInfo(String message) {
        log.infov("{0}: {1}", this.datasourceName, message);
    }

    public void onWarning(Throwable throwable) {
        log.warnv("{0}: {1}", this.datasourceName, throwable.getMessage());
        log.debug("Cause: ", throwable);
    }
}
