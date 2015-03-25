package soaba.core.config;

import flexjson.transformer.AbstractTransformer;

public class ExcludeTransformer extends
        AbstractTransformer {

    @Override
    public Boolean isInline() {
        return true;
    }

    /**
     * This methods always returns null, so that null objects are not serialized.
     */
    @Override
    public void transform(Object object) {
        return;
    }
}
