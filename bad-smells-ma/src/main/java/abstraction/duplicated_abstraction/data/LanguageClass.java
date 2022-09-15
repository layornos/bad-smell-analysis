package abstraction.duplicated_abstraction.data;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "LanguageClass")
public class LanguageClass {
    @Property
    String name = "";
}
