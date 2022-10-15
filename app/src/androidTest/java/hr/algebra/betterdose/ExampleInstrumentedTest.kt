package hr.algebra.betterdose

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import hr.algebra.betterdose.api.druginteraction.DrugInteraction
import hr.algebra.betterdose.api.druginteraction.InteractionFetcher
import hr.algebra.betterdose.api.druglabel.DrugFetcher
import hr.algebra.betterdose.api.druglabel.DrugVM

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    lateinit var instrumentationContext: Context
    lateinit var auth: FirebaseAuth

    val rxcui = "308416"
    val rxcuis = "207106+152923+656659"

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        auth = FirebaseAuth.getInstance()
    }

    @Test
    fun drugApiCallReturnsData() {
        var drug: DrugVM?

        DrugFetcher(instrumentationContext).fetchDrugData(rxcui) {
            drug = it
            assertNotNull(drug)
        }
    }

    @Test
    fun interactionApiCallReturnsData() {
        var interactionVM: DrugInteraction?

        InteractionFetcher(instrumentationContext).fetchInteractions(rxcuis) {
            interactionVM = it
            assertNotNull(interactionVM)
        }
    }

    @Test
    fun logInUser() {
        auth.signInWithEmailAndPassword("test@test.com", "12345678").addOnCompleteListener {
            assertNotNull(it.result?.user)
        }
    }

    @Test
    fun signOutUser() {
        auth.signOut()
        assertNull(auth.currentUser)
    }

    @Test
    fun registerUser() {
        auth.createUserWithEmailAndPassword("newtestuser@test.com", "12345678")
            .addOnCompleteListener {
                assertNotNull(it.result?.user)
            }
    }
}