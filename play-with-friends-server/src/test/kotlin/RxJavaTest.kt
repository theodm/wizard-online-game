import de.theodm.pwf.utils.repeatLastValueDuringSilence
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class RxJavaTest {
    @Before
    fun setUp() = RxJavaPlugins.reset()

    @After
    fun tearDown() = RxJavaPlugins.reset()

    @Test
    fun rxTest() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        val ob = Observable
            .interval(5L, TimeUnit.SECONDS)
            .repeatLastValueDuringSilence()
            .test()

        testScheduler.advanceTimeBy(5L, TimeUnit.SECONDS)
        ob.assertValueAt(0, 0)
        testScheduler.advanceTimeBy(5L, TimeUnit.SECONDS)
        ob.assertValueAt(1, 1)
        testScheduler.advanceTimeBy(5L, TimeUnit.SECONDS)
        ob.assertValueAt(2, 2)
        testScheduler.advanceTimeBy(5L, TimeUnit.SECONDS)
        ob.assertValueAt(3, 3)
    }

    @Test
    fun rx2Test() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        val ob = Observable
            .interval(44L, TimeUnit.SECONDS)
            .repeatLastValueDuringSilence()
            .test()

        testScheduler.advanceTimeBy(44L, TimeUnit.SECONDS)
        ob.assertValueAt(0, 0)
        testScheduler.advanceTimeBy(66L, TimeUnit.SECONDS)
        ob.assertValueAt(1, 0)
        testScheduler.advanceTimeBy(88L, TimeUnit.SECONDS)
        ob.assertValueAt(2, 1)
        testScheduler.advanceTimeBy(110L, TimeUnit.SECONDS)
        ob.assertValueAt(3, 1)
    }
}