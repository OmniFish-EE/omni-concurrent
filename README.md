# omniconcurrent

Partial Jakarta Concurrency Implementatin for GlassFish

Building GF dependencies

From a GlassFish install, use

```
mvn clean install -pl :admin-util,:nucleus-resources,:dol-core,:jts -am -U -DskipTests -T10 -Pfastest
```