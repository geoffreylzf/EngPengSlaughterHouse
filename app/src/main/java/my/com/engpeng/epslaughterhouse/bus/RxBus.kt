package my.com.engpeng.epslaughterhouse.bus

import io.reactivex.subjects.BehaviorSubject


class RxBus {
    companion object {
        val behaviorSubject = BehaviorSubject.create<String?>()
    }
}