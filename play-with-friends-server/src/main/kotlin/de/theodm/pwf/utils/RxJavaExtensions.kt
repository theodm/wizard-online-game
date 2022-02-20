package de.theodm.pwf.utils

import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

// https://stackoverflow.com/questions/11451465/is-there-a-rx-method-to-repeat-the-previous-value-periodically-when-no-values-ar
fun <T> Observable<T>.repeatLastValueDuringSilence(silencePeriodInSeconds: Long = 20L): Observable<T> = Observable
    .switchOnNext(
        this
            .map { data ->
                Observable
                    .interval(silencePeriodInSeconds, TimeUnit.SECONDS)
                    .map { data }
                    .startWithItem(data)
            }
    )