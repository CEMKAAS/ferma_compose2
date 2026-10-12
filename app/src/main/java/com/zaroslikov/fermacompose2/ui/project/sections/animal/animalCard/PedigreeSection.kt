package com.zaroslikov.fermacompose2.ui.project.sections.animal.animalCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.ui.elements.SecondAnimalCard
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.violet_1
import com.zaroslikov.fermacompose2.violet_3

/** Сколько детей видно до «Показать все». */
private const val CHILDREN_PREVIEW = 3

/**
 * Секция «Родословная»: мать, отец и потомство — по нажатию лист с подробностями
 * (в карточку из него отдельной кнопкой). Мать удалена — показывается снимок имени с пометкой «(удалена)»;
 * отец не из хозяйства — имя с пометкой «(не из хозяйства)».
 */
@Composable
fun PedigreeSection(
    state: AnimalCardState,
    onRelativeClick: (PedigreeSheet) -> Unit
) {
    val animal = state.animal
    val hasParents = animal.motherId != null || animal.motherName != null ||
            animal.fatherId != null || animal.fatherName != null

    SecondAnimalCard(
        icon = R.drawable.baseline_pets_24,
        intRes = R.string.pedigree_title
    ) {
        if (hasParents)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val mother = state.mother
                AnimalParameter(
                    titleParameter = R.string.pedigree_mother,
                    parameter = when {
                        mother != null -> mother.name
                        animal.motherId == null && animal.motherName != null ->
                            stringResource(R.string.pedigree_deleted, animal.motherName.orEmpty())

                        else -> animal.motherName ?: stringResource(R.string.pedigree_unknown)
                    },
                    icon = R.drawable.baseline_female_24,
                    iconColor = violet_1,
                    iconColorSecond = violet_3,
                    // Мать и отец — лист с подробностями (в карточку из него отдельной кнопкой)
                    isMore = state.isMotherKnown,
                    onClick = if (state.isMotherKnown) {
                        { onRelativeClick(PedigreeSheet.Mother) }
                    } else null
                )
                HorizontalDivider(thickness = 1.dp, color = gray_4)
                val father = state.father
                AnimalParameter(
                    titleParameter = R.string.pedigree_father,
                    parameter = when {
                        father != null -> father.name
                        animal.fatherId == null && animal.fatherName != null ->
                            stringResource(R.string.pedigree_absent, animal.fatherName.orEmpty())

                        else -> animal.fatherName ?: stringResource(R.string.pedigree_unknown)
                    },
                    icon = R.drawable.baseline_male_24,
                    iconColor = blue_1,
                    iconColorSecond = blue_3,
                    isMore = state.isFatherKnown,
                    onClick = if (state.isFatherKnown) {
                        { onRelativeClick(PedigreeSheet.Father) }
                    } else null
                )
            }

        if (state.children.isNotEmpty())
            ChildrenBlock(children = state.children, onChildClick = { onRelativeClick(PedigreeSheet.Child(it)) })
    }
}

@Composable
private fun ChildrenBlock(
    children: List<DomainAnimalTable>,
    onChildClick: (Long) -> Unit
) {
    val context = LocalContext.current
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.pedigree_children_count, children.size),
            style = text_14,
            color = marengo,
            modifier = Modifier.fillMaxWidth()
        )
        val visible = if (expanded) children else children.take(CHILDREN_PREVIEW)
        // Строки в том же виде, что «Мать» и «Отец»
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            visible.forEachIndexed { index, child ->
                if (index > 0) HorizontalDivider(thickness = 1.dp, color = gray_4)
                val name = if (child.archive) stringResource(R.string.pregnancy_sire_archived, child.name)
                else child.name
                AnimalParameter(
                    titleParameter = when {
                        child.group -> R.string.pedigree_litter
                        child.sex -> R.string.pedigree_son
                        else -> R.string.pedigree_daughter
                    },
                    parameter = if (child.date.isBlank()) name
                    else stringResource(R.string.pedigree_child_value, name, getAgeFromDate(context, child.date)),
                    icon = when {
                        child.group -> R.drawable.baseline_pets_24
                        child.sex -> R.drawable.baseline_male_24
                        else -> R.drawable.baseline_female_24
                    },
                    iconColor = when {
                        child.group -> animal_1
                        child.sex -> blue_1
                        else -> violet_1
                    },
                    iconColorSecond = when {
                        child.group -> green_15
                        child.sex -> blue_3
                        else -> violet_3
                    },
                    isMore = true,
                    onClick = { onChildClick(child.id) }
                )
            }
        }
        if (children.size > CHILDREN_PREVIEW)
            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = stringResource(if (expanded) R.string.pedigree_hide else R.string.pedigree_show_all),
                        style = text_14,
                        color = animal_1
                    )
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = animal_1
                    )
                }
            }
    }
}
