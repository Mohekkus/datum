package contracts.contracts

import contracts.PresetModelAlias

interface PresetViewModelContract {
    fun loadAllPresetsName()
    fun load(name: String)
    fun <T: PresetModelAlias> add(name: String, preset: T)
    fun delete(name: String)
}