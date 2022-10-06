
package ee.omnifish.concurrent.api;

import java.util.Properties;

/**
 * @author David Matejcek
 */
public interface GfManagedThreadFactoryDefinition extends GfContextualResourceDefinition {

    int getPriority();

    void setPriority(int priority);

    Properties getProperties();

    void setProperties(Properties properties);

    void addManagedThreadFactoryPropertyDescriptor(String name, String value);
}
