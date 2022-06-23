# omniconcurrent

Partial Jakarta Concurrency Implementatin for GlassFish.

Forked from Payara code (https://github.com/payara/Payara), which was originally forked from Oracle code (http://github.com/javaee/glassfish)

All copyright of the original copyright holders is acknowleged.

Building GF dependencies

From a GlassFish install, use

```
mvn clean install -pl :admin-util,:nucleus-resources,:dol-core,:jts -am -U -DskipTests -T10 -Pfastest
```

-----------------------

Payara<sup>&reg;</sup> is a trademark of the Payara Foundation.

GlassFish<sup>&reg;</sup> is a trademark of the Eclipse Foundation.
