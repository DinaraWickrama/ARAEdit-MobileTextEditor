package com.example.mobiletexteditor.versioncontrol

import com.example.mobiletexteditor.data.FileDao
import com.example.mobiletexteditor.data.VersionDao
import com.example.mobiletexteditor.data.VersionEntity
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.difflib.patch.Patch
import com.github.difflib.text.DiffRow
import com.github.difflib.text.DiffRowGenerator
/**
 * Implements the "Incremental Versioning (No Duplication)" requirement.
 *
 * - v0 is stored as a full snapshot (there must be at least one full copy somewhere).
 * - every save after that stores only a unified diff patch relative to the
 *   immediately preceding reconstructed version.
 * - reconstructing any version replays patches forward from v0.
 *
 * This keeps the on-disk footprint proportional to the size of each edit,
 * not the size of the whole file, per save.
 */
class DeltaManager(
    private val versionDao: VersionDao,
    private val fileDao: FileDao
) {

    /** Call once, when a file is first opened/created and has no version history yet. */
    suspend fun createBaseVersion(fileId: Long, fullContent: String, label: String? = "Initial version") {
        versionDao.insert(
            VersionEntity(
                fileId = fileId,
                versionNumber = 0,
                delta = fullContent,
                versionName = label
            )
        )
    }

    /**
     * Call on every "save". Diffs [newContent] against the reconstructed latest version
     * and stores only the patch — never the full file again.
     * Returns the new version number, or null if content is unchanged (no-op save).
     */
    suspend fun saveNewVersion(fileId: Long, newContent: String, label: String? = null): Int? {
        val latestVersionNumber = versionDao.latestVersionNumber(fileId)
            ?: throw IllegalStateException("No base version exists for fileId=$fileId. Call createBaseVersion first.")

        val previousContent = reconstructVersion(fileId, latestVersionNumber)
        if (previousContent == newContent) return null // nothing changed, avoid a useless version

        val originalLines = previousContent.lines()
        val revisedLines = newContent.lines()
        val patch = DiffUtils.diff(originalLines, revisedLines)

        val unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
            "v$latestVersionNumber", "v${latestVersionNumber + 1}",
            originalLines, patch, 3
        ).joinToString("\n")

        val newVersionNumber = latestVersionNumber + 1
        versionDao.insert(
            VersionEntity(
                fileId = fileId,
                versionNumber = newVersionNumber,
                delta = unifiedDiff,
                versionName = label
            )
        )
        return newVersionNumber
    }

    /**
     * Rebuilds the full text of [targetVersion] by starting at v0 (the only full copy)
     * and applying each subsequent patch in order.
     */
    suspend fun reconstructVersion(fileId: Long, targetVersion: Int): String {
        val allVersions = versionDao.allForFile(fileId).sortedBy { it.versionNumber }
        require(allVersions.isNotEmpty()) { "No versions stored for fileId=$fileId" }
        require(allVersions.first().versionNumber == 0) { "Missing base version (v0) for fileId=$fileId" }

        var currentLines = allVersions.first().delta.lines()

        for (version in allVersions) {
            if (version.versionNumber == 0) continue
            if (version.versionNumber > targetVersion) break

            val unifiedDiffLines = version.delta.lines()
            val patch: Patch<String> = UnifiedDiffUtils.parseUnifiedDiff(unifiedDiffLines)
            try {
                currentLines = patch.applyTo(currentLines)
            } catch (e: Exception) {
                throw IllegalStateException(
                    "Failed to apply patch for v${version.versionNumber} on fileId=$fileId. " +
                        "Version history may be corrupted.", e
                )
            }
        }
        return currentLines.joinToString("\n")
    }

    /** Returns a human-readable unified diff between two stored versions (for the diff view). */
    suspend fun diffBetween(fileId: Long, fromVersion: Int, toVersion: Int): List<String> {
        val fromText = reconstructVersion(fileId, fromVersion)
        val toText = reconstructVersion(fileId, toVersion)
        val patch = DiffUtils.diff(fromText.lines(), toText.lines())
        return UnifiedDiffUtils.generateUnifiedDiff(
            "v$fromVersion", "v$toVersion", fromText.lines(), patch, 3
        )
    }
    /** Structured, line-tagged comparison (Added / Deleted / Changed / Equal) for the diff UI. */
    suspend fun structuredDiff(fileId: Long, fromVersion: Int, toVersion: Int): List<DiffLine> {
        val fromText = reconstructVersion(fileId, fromVersion)
        val toText = reconstructVersion(fileId, toVersion)

        val generator = DiffRowGenerator.create()
            .showInlineDiffs(false)
            .build()

        val rows = generator.generateDiffRows(fromText.lines(), toText.lines())

        return rows.map { row ->
            val type = when (row.tag) {
                DiffRow.Tag.INSERT -> DiffLineType.ADDED
                DiffRow.Tag.DELETE -> DiffLineType.DELETED
                DiffRow.Tag.CHANGE -> DiffLineType.CHANGED
                DiffRow.Tag.EQUAL -> DiffLineType.EQUAL
            }
            DiffLine(type, row.oldLine, row.newLine)
        }
    }

    /** Rolls the live file back to [targetVersion] and records that rollback as a new version. */
    suspend fun rollbackTo(fileId: Long, targetVersion: Int): String {
        val restoredContent = reconstructVersion(fileId, targetVersion)
        saveNewVersion(fileId, restoredContent, label = "Rollback to v$targetVersion")
        return restoredContent
    }

    suspend fun history(fileId: Long): List<VersionEntity> = versionDao.allForFile(fileId)
}
