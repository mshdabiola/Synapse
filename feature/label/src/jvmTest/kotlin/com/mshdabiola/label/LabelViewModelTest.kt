package com.mshdabiola.label

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.mshdabiola.label.navigation.Label as LabelArg
import com.mshdabiola.testing.fake.repository.FakeLabelRepository
import com.mshdabiola.testing.fake.repository.FakeUserDataRepository
import com.mshdabiola.testing.util.MainDispatcherRule
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LabelViewModelTest {

    @get:Rule(1)
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var labelRepository: FakeLabelRepository
    private lateinit var userDataRepository: FakeUserDataRepository // Use FakeUserDataRepository
    private lateinit var viewModel: LabelViewModel

    private val testLabelArg = LabelArg(isEditMode = false)
    private val testLabelArgEditMode = LabelArg(isEditMode = true)

    @Before
    fun setUp() {
        labelRepository = FakeLabelRepository()
        userDataRepository = FakeUserDataRepository() // Initialize the fake

        // No default coEvery needed as the FakeUserDataRepository has its own default state
    }

    private fun initViewModel(labelArg: LabelArg = testLabelArg) {
        viewModel = LabelViewModel(labelArg, labelRepository, userDataRepository)
    }

}
