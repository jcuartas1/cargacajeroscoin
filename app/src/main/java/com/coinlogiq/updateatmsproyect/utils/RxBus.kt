package com.coinlogiq.updateatmsproyect.utils

import io.reactivex.subjects.PublishSubject
import io.reactivex.Observable

object RxBus {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any){
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}