package org.eclipse.help.internal.ui.util;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.help.internal.util.*;

/**
 * Utiliy class for common tasks
 */

public class Util {
/**
 * Immidiately displays error dialog with a given string,
 * also logs the error using Logger.logError().
 * msg error message to display and log.
 */
public static void displayErrorDialog(String msg) {
	String title = WorkbenchResources.getString("Help_Error");
	Shell parent = getActiveWorkbenchWindow().getShell();
	MessageDialog.openError(parent, title, msg);
	Logger.logError(msg, null);
}
/**
 * Immidiately displays error dialog with a given string,
 * also logs the error using Logger.logError().
 * msg error message to display and log.
 * ex  the exception to be passed to Logger.logError()
 */
public static void displayErrorDialog(String msg, Throwable ex) {
	String title = WorkbenchResources.getString("Help_Error");
	Shell parent = getActiveWorkbenchWindow().getShell();
	MessageDialog.openError(parent, title, msg);
	Logger.logError(msg, ex);
}
/**
 * Immidiately displays an Information dialog with a given string,
 * also logs the info using Logger.logInfo().
 * msg error message to display and log.
 */
public static void displayInfoDialog(String msg) {
	String title = WorkbenchResources.getString("Help_Info");
	Shell parent = getActiveWorkbenchWindow().getShell();
	MessageDialog.openInformation(parent, title, msg);
	Logger.logInfo(msg);
}
/**
 * Immidiately displays a Question dialog with a given string (question).
 * No logging is done.
 * returns which button(Yes/No) was pressed by user
 */
public static boolean displayQuestionDialog(String msg) {
	String title = WorkbenchResources.getString("Help_Question");
	Shell parent = getActiveWorkbenchWindow().getShell();
	return MessageDialog.openQuestion(parent, title, msg);
}
/**
 * Display all errors in the Help Status object. If no errors occurred,
 * or if errors have already been displayed, return.
 */
public static void displayStatus() {
	// show error dialog box if errors have occurred
	if (RuntimeHelpStatus.getInstance().errorsExist()) {
		String title = WorkbenchResources.getString("Help_Error");
		String msg = WorkbenchResources.getString("WE005");
		String errorMessage = RuntimeHelpStatus.getInstance().toString();
		Shell parent = getActiveWorkbenchWindow().getShell();
		
		RuntimeErrorDialog.open(parent, title, msg, errorMessage);

		// for now, reset status object so that errors are not
		// displayed again.
		RuntimeHelpStatus.getInstance().reset();
	}
	return;
}
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
}
