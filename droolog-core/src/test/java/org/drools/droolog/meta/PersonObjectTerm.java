package org.drools.droolog.meta;

import org.drools.droolog.meta.lib.Generated;
import org.drools.droolog.meta.lib.Term;

// the private/internal interface with the getters for structure representation
@Generated
class PersonObjectTerm extends PersonObject implements Term.ObjectTerm {

    PersonMeta.Structure $struct = PersonMeta.Instance.createStructure();

    {
        $struct.bind(this);
    }

    @Override
    public PersonMeta.Structure $getStructure() {
        return $struct;
    }

    @Override
    public void $setStructure(Term.Structure $structure) {
        this.$struct = (PersonMeta.Structure) $structure;
    }

}