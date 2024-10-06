package com.mbradley.mongoidgenerator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class InsertMongoIdAction : AnAction() {
    private var lastMongoIdStartOffset: Int? = null // Track where the MongoID was inserted

    override fun actionPerformed(event: AnActionEvent) {
        // Get the editor instance
        val editor: Editor? = event.getData(CommonDataKeys.EDITOR)
        val document = editor?.document ?: return
        val caretModel = editor.caretModel

        // Determine where to replace or insert the MongoID
        val startOffset = lastMongoIdStartOffset ?: caretModel.offset

        // Generate new MongoID
        val newMongoId = generateMongoId()

        // Perform the document modification within a write command action
        WriteCommandAction.runWriteCommandAction(event.project) {
            // If we've inserted a MongoID before, replace the old one
            val lengthOfPreviousMongoId = 24 // MongoID is always 24 characters long
            if (lastMongoIdStartOffset != null) {
                // Remove the old MongoID
                document.deleteString(startOffset, startOffset + lengthOfPreviousMongoId)
            }

            // Insert the new MongoID
            document.insertString(startOffset, newMongoId)
        }

        // Update the caret position and track the offset of the new MongoID
        lastMongoIdStartOffset = startOffset
        caretModel.moveToOffset(startOffset + newMongoId.length)
    }

    // Function to generate a MongoDB ObjectId
    private fun generateMongoId(): String {
        val timestamp = (System.currentTimeMillis() / 1000).toInt()
        val machineIdentifier = (Math.random() * 0xFFFFFF).toInt()
        val processIdentifier = (Math.random() * 0xFFFF).toInt()
        val counter = (Math.random() * 0xFFFFFF).toInt()

        return String.format("%08x%06x%04x%06x", timestamp, machineIdentifier, processIdentifier, counter)
    }
}