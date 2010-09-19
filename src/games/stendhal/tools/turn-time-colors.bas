'/* $Id$ */
'/***************************************************************************
' *                   (C) Copyright 2003-2010 - Stendhal                    *
' ***************************************************************************
' ***************************************************************************
' *                                                                         *
' *   This program is free software; you can redistribute it and/or modify  *
' *   it under the terms of the GNU General Public License as published by  *
' *   the Free Software Foundation; either version 2 of the License, or     *
' *   (at your option) any later version.                                   *
' *                                                                         *
' ***************************************************************************/


'* Excel Visual Basic Macro to color turn time overflows.
'*
'* grep "overflow" logdatei.log
'* The removed unused columns so that the rest looks like this:
'* Time     total "ms:" ...
'* 00:05:04   46  ms:    0   0   0   188 188 326 340 340 346 346 346



'* Adjust these values to your table
Private Const START_COLUMN = 4
Private Const NUMBER_OF_ROWS = 450


'______________________________________________________________


'* Internal Consts, adjust only if you know what you are doing
Private Const NUMBER_OF_MEASURED_VALUES = 10
Private Const WARN_TIME = 200 'ms
Private Const COLOR_INDEX = 6

'* Main Program
Sub Main()
    For i% = 1 To NUMBER_OF_ROWS
        For j% = START_COLUMN To START_COLUMN + NUMBER_OF_MEASURED_VALUES

            oldV& = getValue(IndexToCellName(i%, j% - 1))
            newV& = getValue(IndexToCellName(i%, j%))

            If (oldV& + WARN_TIME < newV&) Then
                ColorCellName (IndexToCellName(i%, j%))
            End If

        Next
    Next
End Sub

'* Calculates the CellName ("A1", "B23") based on row and column indices
'* Important Note: It can only handle the first 26 columns
Function IndexToCellName$(i%, j%)
    ' TODO: Make it work  for more than 65 columns
    IndexToCellName = Chr(65 + j% - 1) & Trim(Str(i%))
End Function

'* Gets the integer value of a column.
'* Note: The term "ms:" is converted to 0
Function getValue&(Cell$)
    res$ = Trim(Range(Cell$).Text)
    ' Hack: the column - 1 contains "ms:"
    If (res$ = "ms:") Then
        resV& = 0
    Else
        resV& = Val(res$)
    End If
    getValue& = resV&
End Function

'* Colors a cell
Sub ColorCellName(Cell$)
    Range(Cell$).Select
    With Selection.Interior
        .ColorIndex = COLOR_INDEX
        .Pattern = xlSolid
    End With
End Sub
