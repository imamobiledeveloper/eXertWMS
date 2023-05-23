package com.exert.wms

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object SessionExpirationObject {

    private val subject: PublishSubject<Unit> = PublishSubject.create()
    val observable: Observable<Unit> = subject

    fun sessionExpired() {
        subject.onNext(Unit)
    }
}
