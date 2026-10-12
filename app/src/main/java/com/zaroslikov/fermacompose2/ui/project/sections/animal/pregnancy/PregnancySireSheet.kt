@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaroslikov.fermacompose2.ui.project.sections.animal.pregnancy

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zaroslikov.domain.models.DomainAnimalTable.DomainAnimalTable
import com.zaroslikov.domain.models.enums.AppIcon
import com.zaroslikov.fermacompose2.R
import com.zaroslikov.fermacompose2.animal_1
import com.zaroslikov.fermacompose2.animal_2
import com.zaroslikov.fermacompose2.blue_1
import com.zaroslikov.fermacompose2.blue_3
import com.zaroslikov.fermacompose2.dark
import com.zaroslikov.fermacompose2.gray_4
import com.zaroslikov.fermacompose2.gray_7
import com.zaroslikov.fermacompose2.green_15
import com.zaroslikov.fermacompose2.marengo
import com.zaroslikov.fermacompose2.orang_13
import com.zaroslikov.fermacompose2.supportFun.getAgeFromDate
import com.zaroslikov.fermacompose2.ui.elements.BaseBottomSheet
import com.zaroslikov.fermacompose2.ui.elements.CardClips
import com.zaroslikov.fermacompose2.ui.elements.CountColorGradientCard
import com.zaroslikov.fermacompose2.ui.elements.GradientButton
import com.zaroslikov.fermacompose2.ui.elements.text_14
import com.zaroslikov.fermacompose2.ui.elements.text_16
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.AnimalParameter
import com.zaroslikov.fermacompose2.ui.project.sections.animal.list_screen.IconAnimal

/** Лист «Отец» на экране беременности: самец активной беременности + число случек с самкой. */
@Composable
fun PregnancySireSheet(
    state: AnimalPregnancyState,
    onDismissRequest: () -> Unit,
    onOpenCard: (Long) -> Unit
) {
    val active = state.active ?: return
    ParentInfoSheet(
        supText = state.animal.name,
        sire = state.sireDetails,
        isLoaded = state.isSireDetailsLoaded,
        externalName = active.sireName?.takeIf { active.sireId == null && it.isNotBlank() },
        snapshotName = active.sireName,
        childrenCount = state.sireChildrenCount,
        matingsCount = state.sireMatingsCount,
        onDismissRequest = onDismissRequest,
        onOpenCard = onOpenCard
    )
}

/**
 * Лист «Мать» / «Отец» / «Потомок»: подробности о животном без перехода в его карточку — переход
 * отдельной кнопкой [onOpenCard], только для животного из хозяйства. Общий для экрана
 * беременности и родословной в карточке животного (родители и потомство).
 *
 * [externalName] — чужой производитель (в хозяйстве его нет); [snapshotName] — имя,
 * сохранённое на случай, если животное удалили; [matingsCount] = null — строку не показываем.
 */
@Composable
fun ParentInfoSheet(
    supText: String,
    isFemale: Boolean = false,
    /** Свой заголовок (например, «Потомок»); по умолчанию «Мать»/«Отец» по [isFemale]. */
    title: String? = null,
    /** Своя иконка заголовка (например, для группы-помёта); по умолчанию ♀/♂ по [isFemale]. */
    @DrawableRes icon: Int? = null,
    sire: DomainAnimalTable?,
    isLoaded: Boolean,
    externalName: String?,
    snapshotName: String?,
    childrenCount: Int,
    matingsCount: Int?,
    onDismissRequest: () -> Unit,
    onOpenCard: (Long) -> Unit
) {
    BaseBottomSheet(
        title = title ?: stringResource(if (isFemale) R.string.pedigree_mother else R.string.pedigree_father),
        supText = supText,
        iconRes = icon ?: if (isFemale) R.drawable.baseline_female_24 else R.drawable.baseline_male_24,
        colors = listOf(animal_1, animal_2),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                externalName != null -> {
                    SireHeader(name = externalName, type = null, animal = null, isFemale = isFemale)
                    Text(
                        text = stringResource(R.string.pregnancy_sire_sheet_external),
                        style = text_14,
                        color = gray_7
                    )
                    matingsCount?.let { MatingsParameter(it) }
                }

                !isLoaded ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = animal_1, modifier = Modifier.size(32.dp))
                    }

                sire == null -> {
                    // Самец удалён: показываем сохранённое имя
                    snapshotName?.takeIf { it.isNotBlank() }?.let {
                        SireHeader(name = it, type = null, animal = null, isFemale = isFemale)
                    }
                    Text(
                        text = stringResource(if (isFemale) R.string.pedigree_sheet_missing_female else R.string.pregnancy_sire_sheet_missing),
                        style = text_14,
                        color = gray_7
                    )
                    matingsCount?.let { MatingsParameter(it) }
                }

                else -> {
                    SireHeader(name = sire.name, type = sire.type, animal = sire, isFemale = isFemale)
                    SireDetails(
                        sire = sire,
                        childrenCount = childrenCount,
                        matingsCount = matingsCount
                    )
                    GradientButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.pregnancy_sire_sheet_open_card),
                        colors = listOf(animal_1, animal_2),
                        postfixIconRes = R.drawable.baseline_chevron_right_24,
                        isShadow = true,
                        paddingValues = PaddingValues(16.dp)
                    ) { onOpenCard(sire.id) }
                }
            }
        }
    }
}

/** Иконка/фото, имя, тип, пол и отметка «В архиве». */
@Composable
private fun SireHeader(name: String, type: String?, animal: DomainAnimalTable?, isFemale: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconAnimal(
            sex = if (animal?.group == true) null else animal?.sex ?: !isFemale,
            currentIcon = animal?.currentIcon ?: AppIcon.PETS,
            imagePath = animal?.imagePath,
            isArchive = animal?.archive ?: false
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = text_16,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = marengo
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                type?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = text_14,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                if (animal?.group != true)
                    CountColorGradientCard(sex = animal?.sex ?: !isFemale)
                if (animal?.archive == true)
                    CardClips(
                        colorBackground = gray_4,
                        colorBorder = gray_4,
                        colorText = dark,
                        icon = R.drawable.baseline_archive_24,
                        colorIcon = gray_7,
                        value = stringResource(R.string.pregnancy_sire_sheet_archive)
                    )
            }
        }
    }
}

@Composable
private fun SireDetails(sire: DomainAnimalTable, childrenCount: Int, matingsCount: Int?) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (sire.date.isNotBlank())
            AnimalParameter(
                titleParameter = R.string.animal_list_age,
                parameter = getAgeFromDate(context, sire.date) + " (${sire.date})",
                icon = R.drawable.baseline_calendar_month_24,
                iconColor = animal_1,
                iconColorSecond = green_15
            )
        sire.dateFactory?.takeIf { it.isNotBlank() }?.let {
            AnimalParameter(
                titleParameter = R.string.animal_list_date_factory,
                parameter = getAgeFromDate(context, it) + " ($it)",
                icon = R.drawable.baseline_event_24,
                iconColor = animal_1,
                iconColorSecond = green_15
            )
        }
        AnimalParameter(
            titleParameter = R.string.pregnancy_sire_sheet_offspring,
            parameter = childrenCount.toString(),
            icon = R.drawable.baseline_pets_24,
            iconColor = blue_1,
            iconColorSecond = blue_3
        )
        matingsCount?.let { MatingsParameter(it) }
        if (sire.note.isNotBlank())
            AnimalParameter(
                titleParameter = R.string.pregnancy_note,
                parameter = sire.note,
                icon = R.drawable.baseline_sticky_note_2_24,
                iconColor = gray_7,
                iconColorSecond = orang_13
            )
    }
}

@Composable
private fun MatingsParameter(count: Int) {
    AnimalParameter(
        titleParameter = R.string.pregnancy_sire_sheet_matings,
        parameter = count.toString(),
        icon = R.drawable.baseline_favorite_24,
        iconColor = animal_1,
        iconColorSecond = green_15
    )
}
