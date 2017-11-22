package com.cxd.android.liveplayer.toolkit.rxjava.executor;


import android.support.annotation.Nullable;

import com.cxd.android.liveplayer.toolkit.rxjava.bus.thread.EventThread;
import com.cxd.android.liveplayer.utils.KasLog;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by Rancune@126.com on 2016/10/22.
 * <p>
 * we SHOULD ALWAYS remember to DISPOSE the return value of these static methods
 */

@SuppressWarnings("unused")
public class RxExecutor {
    private static final String TAG = "RxExecutor";
    
    // 用于返回一个空的已经disposed的Disposable，而不是null，有利于减少忘记判断null的情况
    public static final Disposable EMPTY = new Disposable() {
        @Override
        public void dispose() {
            // do nothing
        }
        
        @Override
        public boolean isDisposed() {
            return true;
        }
    };
    
    /**
     * @param thread   in which thread we call the function
     * @param function the function to be called
     * @param callback the callback
     * @param params   the params of the function
     * @param <T>      the return value type of the function
     * @return a disposable which can be dispose
     */
    public static <T> Disposable execute(EventThread thread,
                                         final Function<? extends T> function,
                                         @Nullable final RxCallBack<? super T> callback,
                                         @Nullable final Object... params) {
        if (function == null) {
            return EMPTY;
        }
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                if (!e.isCancelled()) {
                    T result = function.call(params);
                    e.onNext(result);
                    e.onComplete();
                }
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(EventThread.getScheduler(thread))
                .doOnSubscribe(new Consumer<Subscription>() {
                    @Override
                    public void accept(Subscription subscription) throws Exception {
                        if (callback != null) {
                            callback.start();
                        }
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        if (callback != null) {
                            callback.success(t);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (callback != null) {
                            callback.failture();
                        }
                        throwable.printStackTrace();
                    }
                });
    }
    
    
    /**
     * @param thread in which thread we call the function
     * @param action the function to be called
     * @return a disposable which can be dispose
     */
    public static Disposable action(EventThread thread,
                                    final Action action) {
        if (action == null) {
            return EMPTY;
        }
        final Scheduler.Worker worker = EventThread.getScheduler(thread).createWorker();
        worker.schedule(new Runnable() {
            @Override
            public void run() {
                if (!worker.isDisposed()) {
                    action.call();
                }
            }
        });
        return worker;
    }
    
    /**
     * @param thread   in which thread the runnable will be executed
     * @param runnable the runnabe to be executed
     * @return a disposable which can be dispose
     */
    public static Disposable post(EventThread thread,
                                  final Runnable runnable) {
        if (runnable == null) {
            return EMPTY;
        }
        Scheduler.Worker worker = EventThread.getScheduler(thread).createWorker();
        worker.schedule(runnable);
        return worker;
    }
    
    /**
     * @param thread         in which thread the runnable will be executed
     * @param millionSeconds the delay time in MILLISECONDS
     * @param runnable       the runnabe to be executed
     * @return a disposable which can be dispose
     */
    public static Disposable postDelayed(EventThread thread,
                                         final long millionSeconds,
                                         final Runnable runnable) {
        if (runnable == null) {
            return EMPTY;
        }
        Scheduler.Worker worker = EventThread.getScheduler(thread).createWorker();
        worker.schedule(runnable, millionSeconds, TimeUnit.MILLISECONDS);
        return worker;
    }
    
    /**
     * @param thread       in which thread the runnable will be executed
     * @param initialDelay time to wait before executing the action for the first time
     * @param runnable     the Runnable to execute periodically
     * @param period       the time interval to wait each time in between executing the action
     * @return a disposable which can be dispose
     */
    public static Disposable schedulePeriodically(EventThread thread,
                                                  final long initialDelay,
                                                  Runnable runnable,
                                                  final long period) {
        if (runnable == null) {
            return EMPTY;
        }
        Scheduler.Worker worker = EventThread.getScheduler(thread).createWorker();
        worker.schedulePeriodically(runnable, initialDelay, period, TimeUnit.MILLISECONDS);
        return worker;
    }
    
    public static <T> Disposable just(final EventThread thread, final T item, final Action1<? super T> action) {
        if (action == null) {
            return EMPTY;
        }
        return Flowable.just(item)
                .subscribeOn(EventThread.getScheduler(thread))
                .observeOn(EventThread.getScheduler(thread))
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        action.call(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        KasLog.e(TAG, throwable.toString());
                    }
                });
    }
    
    
}
