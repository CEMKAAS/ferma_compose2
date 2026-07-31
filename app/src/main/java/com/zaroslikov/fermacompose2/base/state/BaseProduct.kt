package com.zaroslikov.fermacompose2.base.state

import com.zaroslikov.domain.models.dto.shared.DomainCountSuffix
import com.zaroslikov.domain.models.enums.Suffix

interface BaseProductState {
    val product: Product
    val pickList: BasePickList
    val errors: ProductError
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
    val price: String
    val priceAll: String
    val isAutoPrice: Boolean
}

interface BasePickList {
    val categories: List<String>
    val warehouseList: List<DomainCountSuffix>
}

interface BaseError {
    val hasAnyError: Boolean
}

interface ProductError : BaseError {
    val isErrorTitle: Boolean
    val isErrorSlash: Boolean
    val isErrorCount: Boolean
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
    val isDate: Boolean
    val isNote: Boolean
    val isMultiProjectTemplate: Boolean
}