package com.cxd.android.liveplayer.toolkit.rxjava.bus.finder;

import com.cxd.android.liveplayer.toolkit.rxjava.bus.entity.EventType;
import com.cxd.android.liveplayer.toolkit.rxjava.bus.entity.ProducerEvent;
import com.cxd.android.liveplayer.toolkit.rxjava.bus.entity.SubscriberEvent;

import java.util.Map;
import java.util.Set;

/**
 * Finds producer and subscriber methods.
 */
public interface Finder {

    Map<EventType, ProducerEvent> findAllProducers(Object listener);

    Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener);


    Finder ANNOTATED = new Finder() {
        @Override
        public Map<EventType, ProducerEvent> findAllProducers(Object listener) {
            return AnnotatedFinder.findAllProducers(listener);
        }

        @Override
        public Map<EventType, Set<SubscriberEvent>> findAllSubscribers(Object listener) {
            return AnnotatedFinder.findAllSubscribers(listener);
        }
    };
}
