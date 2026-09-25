package cc.shinemoon.datum.viewmodel

import cc.shinemoon.datum.model.preset.PresetModel
import cc.shinemoon.datum.model.preset.TopologyRules
import cc.shinemoon.datum.testutil.sampleInspectionData
import cc.shinemoon.datum.types.preset.MetricStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PresetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUseCase: FakeDatabaseUseCase
    private lateinit var viewModel: PresetViewModel
    private val inspectionData = sampleInspectionData()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUseCase = FakeDatabaseUseCase()
        viewModel = PresetViewModel(fakeUseCase, inspectionData)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun savePreset_savesToUseCase_andUpdatesState() = runTest(testDispatcher) {
        val preset = PresetModel(name = "Original", topologyRules = TopologyRules(requireValidBRep = true))
        viewModel.savePreset("Solid Check", preset)
        advanceUntilIdle()

        val saved = fakeUseCase.get("Solid Check")
        assertNotNull(saved)
        assertEquals("Solid Check", saved.name)
        assertTrue(saved.topologyRules.requireValidBRep)
        assertEquals(listOf("Solid Check"), viewModel.savedPresetsName.value)
        assertEquals("Solid Check", viewModel.preset.value.name)
        assertNull(viewModel.presetMessage.value)
    }

    @Test
    fun savePreset_blankName_setsErrorMessage() = runTest(testDispatcher) {
        viewModel.savePreset("   ")
        advanceUntilIdle()

        assertEquals("Preset name is required.", viewModel.presetMessage.value)
        assertTrue(fakeUseCase.getAllNames().isEmpty())
    }

    @Test
    fun savePreset_duplicateName_setsErrorMessage() = runTest(testDispatcher) {
        fakeUseCase.add("Existing", PresetModel(name = "Existing"))

        viewModel.savePreset("Existing")
        advanceUntilIdle()

        assertEquals("Preset \"Existing\" already exists.", viewModel.presetMessage.value)
    }

    @Test
    fun loadPreset_updatesState_andEvaluation() = runTest(testDispatcher) {
        val model = PresetModel(name = "TestRule", topologyRules = TopologyRules(requireValidBRep = true))
        fakeUseCase.add("TestRule", model)

        viewModel.loadPreset("TestRule")
        advanceUntilIdle()

        assertEquals("TestRule", viewModel.preset.value.name)
        val eval = viewModel.evaluation.value
        assertNotNull(eval)
        assertEquals(MetricStatus.PASS, eval.checkStatus("topology.validBRep"))
    }

    @Test
    fun updateSavedPreset_updatesStorageAndState() = runTest(testDispatcher) {
        fakeUseCase.add("MyPreset", PresetModel(name = "MyPreset"))
        val updatedModel = PresetModel(name = "MyPreset", topologyRules = TopologyRules(requireClosedSolid = true))

        viewModel.updateSavedPreset(updatedModel)
        advanceUntilIdle()

        val retrieved = fakeUseCase.get("MyPreset")
        assertNotNull(retrieved)
        assertTrue(retrieved.topologyRules.requireClosedSolid)
        assertNull(viewModel.presetMessage.value)
    }

    @Test
    fun updateSavedPreset_nonExistent_setsErrorMessage() = runTest(testDispatcher) {
        viewModel.updateSavedPreset(PresetModel(name = "NonExistent"))
        advanceUntilIdle()

        assertEquals("Preset \"NonExistent\" no longer exists.", viewModel.presetMessage.value)
    }

    @Test
    fun deletePreset_removesFromStorage_andResetsState() = runTest(testDispatcher) {
        fakeUseCase.add("ToDelete", PresetModel(name = "ToDelete"))

        viewModel.deletePreset("ToDelete")
        advanceUntilIdle()

        assertNull(fakeUseCase.get("ToDelete"))
        assertTrue(viewModel.savedPresetsName.value.isEmpty())
        assertEquals("", viewModel.preset.value.name)
    }

    @Test
    fun loadAllPresetsName_populatesState() = runTest(testDispatcher) {
        fakeUseCase.add("P1", PresetModel(name = "P1"))
        fakeUseCase.add("P2", PresetModel(name = "P2"))

        viewModel.loadAllPresetsName()
        advanceUntilIdle()

        assertEquals(listOf("P1", "P2"), viewModel.savedPresetsName.value)
    }

    @Test
    fun close_delegatesToUseCase() = runTest(testDispatcher) {
        viewModel.close()
        advanceUntilIdle()

        assertTrue(fakeUseCase.closed)
    }
}
