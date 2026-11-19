# Design

## Source of truth

Active, 2026-11-04. Scope: task details (`todo/ui/task_details`) and its link/comment sheets. This contract does not redesign the remaining task screens.

Evidence: `app/src/main/java/com/zaroslikov/fermacompose2/ui/elements/BottomSheet.kt` (`BaseBottomSheet`), `todo/src/main/java/com/zaroslikov/todo/ui/components/TodoInputs.kt`, `todo/src/main/java/com/zaroslikov/todo/ui/components/TodoScaffoldParts.kt`, `todo/src/main/java/com/zaroslikov/todo/ui/theme/TodoColors.kt`, `todo/src/main/java/com/zaroslikov/todo/ui/theme/TodoTextStyles.kt`. No dedicated task-details mockup was supplied. Existing task UI contains explicit iOS styling; the user requests alignment with the farm app.

## Brand

Practical, calm farm management. Use the app's white surfaces, green actions, neutral text, familiar Material controls. Avoid iOS grouped-settings presentation, uppercase group captions, saturated icon tiles and a centered title flanked by text actions.

## Product goals

Make task details feel part of «Моё Хозяйство 2» while keeping all editing actions available. Success: consistent typography, controls and palette; unchanged task persistence and navigation. Database and other task screens are outside this change.

## Personas and jobs

Assumption based on the project: a farm owner edits deadlines, notes and work progress, adds subtasks and coordinates related tasks. The form must remain usable during quick edits on a phone.

## Information architecture

Keep the existing bottom sheet reached from lists and smart lists. Order: title/note, deadline, «Параметры задачи» (status, priority and tags together in one outlined group with dividers), optional section, subtasks, links, comments, deletion. Opening a related task replaces sheet content; a subtask exposes a separate parent-navigation row beneath the header.

## Design principles

Reuse the app's patterns before adding visual primitives. Keep labels visible, allow meaningful values to wrap, use color for state and entity identity. Closing the form must preserve the existing draft-save behavior.

## Visual language

White background; `black_2` primary text, `gray_7` secondary text, `grey_2` separators, `gray_8` field borders. Actions use `green_shamrock`; status, priority and tag colors retain their meaning. Tag chips use their full solid color without a separate dot, with always-white text and a white checkmark when selected. This also applies to tag chips in list filters; priority chips keep their existing appearance. Header uses `textBold_20`, section headings `textBold_16`, body `text_16`, metadata `text_14`, all inherited from app typography. Horizontal page inset 16dp, form spacing 12–16dp, field/section radius 12dp. Icons use pale tinted backgrounds and colored glyphs. Use existing Material motion; no new animation or imagery.

## Components

Task links (2026-11-11): creation offers «Связана с», «Блокирует» and «Заблокирована задачей», relative to the open task. Use a chain for ordinary links, a key for outgoing blocking links and a lock for incoming blocking links. Task rows show each applicable relation separately when a task has several kinds of links; accessible descriptions explain direction and count. These additions apply to task rows, subtasks and task details. Direction uses the existing `blocks` relation with reversed endpoints where needed; database and backup formats stay unchanged. Read-only query projections may expose direction counts for the row indicators.

Header follows `BaseBottomSheet`: left title, close icon, neutral divider. Text fields follow `TodoTextField`'s outline and floating-label pattern while retaining existing focus and IME callbacks. Material switches, checkboxes, menus, chips and pickers remain the interaction vocabulary. Local section containers have subtle outlines; destructive action is an outlined button with delete icon. Nested forms reuse `TodoBottomSheet`, `TodoTextField` and `TodoSegmentedButtons`. «Новый тег» opens the shared `TodoTitleColorSheet` with required name and palette selection; creation attaches the tag to the current task, while dismissal discards the draft. Token ownership remains `todo/ui/theme`; do not change its shared palette globally for this screen.

Tags (2026-11-13): show «Теги» as a property row matching «Приоритет», with an icon, label, selected tags as solid cards in their own colors, without an up/down indicator. Cards show white titles, wrap compactly to additional lines, and open the picker when tapped; the summary shows only saved tags. Tapping opens a nested bottom sheet containing all project tags with multiple selection and a «Новый тег» chip after the tags. Selection is a local draft until «Сохранить» applies it to the task and returns to details. Dismissal, Back and the header cross discard the draft. The shared title/color editor returns to the picker after creation or cancellation; newly created project tags are selected in the draft and attach to the task only on Save. Bottom actions are «Сброс» (clear the draft selection and stay in the picker) and «Сохранить». A cross opposite the «Теги» heading closes both the picker and task details.

## Accessibility

Use platform Material touch targets (48dp), persistent field labels, localized icon descriptions and semantic section headings. Meaningful status information includes text, and completion uses checkbox state. Preserve font scaling and keyboard actions; avoid fixed-height text containers. No formal accessibility certification is claimed.

## Responsive behavior

Use the existing sheet width behavior and one vertical scroll region. Status and priority values sit below labels so long values do not compete for horizontal space. Parent titles may span two lines. Subtasks and comments wrap; controls retain their touch size. No custom tablet breakpoint is introduced.

## Interaction states

Assignees (2026-11-14): tasks and subtasks have independent lists of executor names in «Подробно». Show an «Исполнители» section with saved names and an individually labelled remove action, followed by a single-line «Имя исполнителя» field and an add action. Adding saves immediately; blank names and duplicates ignoring case and surrounding whitespace are rejected. Clear the input after adding and when switching tasks. Names remain available offline, in recurring tasks and in JSON backups; older tasks and backups start with an empty list. Preserve existing data through a Room migration.

Assignee selection: remember added names in a directory belonging to the task's project. Offer existing names while entering an executor, filter by the entered text and exclude names already assigned to the current task. A user may select a saved name or enter a new one. Tasks and subtasks in all lists of the same project share this directory; another project never supplies suggestions. Removing an assignment or deleting a task keeps the name available for reuse. Include the directory in backup and project deletion; populate it from existing task assignments on migration and from older backups on import.

Assignee picker presentation (2026-11-15): use Material 3 `DropdownMenu` and `DropdownMenuItem` anchored to the name field. The picker floats above content instead of expanding the form. Keep typing and the keyboard available while filtering, dismiss on outside tap, and close after selecting a name. Retain the explicit «Выбрать исполнителя» trigger.

Match the picker width to the measured input field, use the same 12dp rounded corners as the field, and separate adjacent executor entries with neutral dividers. Do not add a trailing divider after the last entry.

Assigned executors: separate saved names with neutral dividers inside the outlined group. Show the first three in their saved order by default; when there are more, use the same full-width «Показать все» / «Свернуть» control as subtasks. Reset expansion when switching tasks. Animate addition, removal, expansion and collapse with the adjacent sections' fade and vertical size transitions. Retain outgoing rows until their exit finishes, disable their actions, and keep the empty group until the last row finishes disappearing.

When the last assigned name disappears, animate the group's following 8dp gap as well. Removing the retained row snapshot must not cause an additional jump of the input field after its exit animation.

Assignee metadata: task and subtask rows show their saved executor names joined with «, » as secondary text. Use the same display in normal lists, smart lists, column cards and the subtask rows inside details. Preserve saved name order, allow names to wrap, and omit the metadata line when nobody is assigned. A subtask displays its own executors.

Precede executor metadata with a neutral 14dp person icon and 4dp spacing, matching the neighboring metadata icons.

Assignee input focus: remove the dropdown arrow opposite the «Исполнители» heading. Open the picker from the name field. Tapping outside the input clears its focus and dismisses the picker; tapping inside preserves editing and text selection. Outside taps must still activate their controls, and tapping another text field transfers focus normally.

Repeat (2026-11-14): «Повтор» appears in «Срок» only when a date is enabled. It opens a nested sheet with no repeat / daily / weekly / monthly / yearly choices and optional inclusive end date. Save applies the draft; dismissal discards it. Disabling the due date clears repetition. Completing a repeating task keeps its history and creates the next dated task; recurrence settings use stable codes in Room and backup, with a non-destructive migration.

Task rows, column cards and details subtasks show a repeat icon with the period in their metadata. Both use the existing overdue red for incomplete tasks after their date/time deadline, and neutral gray otherwise; ordinary tasks have no repeat indicator.

Subtasks (2026-11-13): show the first three in their saved order, with «Показать все» / «Свернуть» inside the card when there are more. Keep the total completion count and progress for all subtasks. Place «Новая подзадача» outside the card, matching the standalone comment input with a trailing send action; keep keyboard submission available. Hide the empty card when there are no subtasks.

The progress row spans the same width as the subtask card, without horizontal inset. Its bar fills the available space, with the rounded completion percentage on the right, calculated across all subtasks including hidden ones.

Subtask rows use the shared round completion control, priority marks immediately before the title, solid colored tag cards with white text, and a comment icon with a numeric counter. Hide the redundant planned/done status labels; retain in-progress and custom statuses. Completed titles remain struck through.

Subtask expansion, collapse, insertion and deletion use the same fade and vertical expansion/shrink transitions as comments. Retain outgoing row snapshots through their exit, preserve repository ordering and reverse transitions on rapid toggles. The last row finishes its exit before the card disappears; outgoing rows no longer dispatch task actions.

Show comment and link counters together in one row, retaining each counter's accessible description. The standalone new-subtask field uses an in-field placeholder instead of a floating label so the entered title stays vertically centered; other details fields retain their labels.

Keep the loading indicator, per-task scroll reset, date/time enabled states, empty link-search feedback, disabled blank-comment submission, text validation, deletion confirmations and snackbar errors. Offline operation and repository behavior stay unchanged. Maintain navigation-bar and keyboard clearance, including nested edit/search sheets.

## Content voice

Russian, concise and consistent with existing task terminology: «Подробно», «Примечание», «Подзадачи», «Комментарии». Use ordinary case for section titles. Closing uses an icon with the accessible description «Закрыть подробности».

## Implementation constraints

Jetpack Compose/Material 3 with no new dependencies. Keep MVI intents and save-on-focus-loss behavior. For link direction indicators, read-only repository projections may expose counts; migrations, persisted data formats and shared theme behavior remain unchanged. Validate Kotlin compilation and inspect the installed sheet on the existing emulator; visual checks should cover keyboard input, scroll, parent navigation and nested forms when feasible.

## Open questions

None blocking. Device-specific font scaling and tablet presentation remain part of normal app QA; the user supplied no additional device or visual-reference requirements.
