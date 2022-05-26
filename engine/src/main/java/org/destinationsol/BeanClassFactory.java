package org.destinationsol;

import org.terasology.gestalt.di.BeanContext;
import org.terasology.gestalt.util.reflection.ClassFactory;

import javax.inject.Provider;
import java.util.Optional;

public class BeanClassFactory implements ClassFactory {
    private final Provider<BeanContext> beanContext;

    public BeanClassFactory(Provider<BeanContext> beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public <T> Optional<T> instantiateClass(Class<? extends T> type) {
        return (Optional<T>) beanContext.get().findBean(type);
    }
}