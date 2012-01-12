package org.husio.api;

/**
 * A module that requires initialization and resource dealocation.
 * @author rafael
 */
public interface Initializable {
    public void start() throws Exception;
    public void stop() throws Exception;
}
