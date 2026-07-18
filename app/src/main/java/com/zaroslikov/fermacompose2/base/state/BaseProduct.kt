package com.zaroslikov.fermacompose2.base.state

import com.zaroslikov.domain.models.enums.Suffix

interface BaseProductState {
    val product: Product
    val pickList: BasePickList
    val errors: BaseError
    val template: TemplateState
}

interface Product {
    val itemId: Long
    val title: String
    val date: String
    val count: String
    val countSuffix: Suffix
    val category: String
    val note: String
    val projectId: Long
    val isEntry: Boolean

}

interface BasePickList

interface BaseError{
    val hasAnyError: Boolean
}

interface TemplateState {
    val name: String
    val isTemplate: Boolean
    val isTemplateEntry: Boolean
    val pin: Boolean
    val activeField: ActiveField
}

interface ActiveField {
    val isTitle: Boolean
    val isCount: Boolean
    val isSuffix: Boolean
    val isCategory: Boolean
    val isNote: Boolean
    val isMultiProjectTemplate: Boolean
}