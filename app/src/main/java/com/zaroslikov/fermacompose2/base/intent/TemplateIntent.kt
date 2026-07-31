package com.zaroslikov.fermacompose2.base.intent


import com.zaroslikov.fermacompose2.ui.project.sections.add.list_screen.TemplateItem


sealed class TemplateIntent : BaseIntent {
    data class OpenTemplateDeleteBottomSheet(val value: Long? = null) : TemplateIntent()
    data class SetPinOfTemplateClick(val value: Pair<Boolean, Long>) : TemplateIntent()
    data class OpenPatternsBottomSheetClick(val value: Boolean) : TemplateIntent()
    data class LoadDataForTemplateBottomSheetClick(val value: Long) : TemplateIntent()

    data class OpenTemplateEditor(
        val isOpen: Boolean,
        val id: Long? = null,
        val isTemplate: Boolean = false
    ) : TemplateIntent()

    data class LoadDataForTemplate(val value: List<TemplateItem>) : TemplateIntent()

    data object InsertTemplate : TemplateIntent()
    data object UpdateTemplate : TemplateIntent()
    data object DeleteTemplate : TemplateIntent()
}