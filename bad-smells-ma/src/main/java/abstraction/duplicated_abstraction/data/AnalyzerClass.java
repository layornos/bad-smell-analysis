package abstraction.duplicated_abstraction.data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "AnalyzerClass")
public class AnalyzerClass {
    @Property
    public String name = "";

    @Relationship(type = "uses", direction = Relationship.UNDIRECTED)
    public Set<LanguageClass> languageClasses = new HashSet<>();
}
